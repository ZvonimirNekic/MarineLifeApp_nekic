package org.unizd.rma.nekic

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button

import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.unizd.rma.nekic.adapter.MarineLifeViewAdapter
import org.unizd.rma.nekic.databinding.ActivityMainBinding
import org.unizd.rma.nekic.models.MarineLife
import org.unizd.rma.nekic.utils.Status
import org.unizd.rma.nekic.utils.clearEditText
import org.unizd.rma.nekic.utils.longToastShow
import org.unizd.rma.nekic.utils.setupDialog
import org.unizd.rma.nekic.utils.validateEditText
import org.unizd.rma.nekic.viewmodels.MarineLifeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var captureIV: ImageView
    private lateinit var captureIV2: ImageView
    private lateinit var imageUri: Uri
    private var selectedOption: String = ""
    private val contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        captureIV.setImageURI(null)
        captureIV.setImageURI(imageUri)
        captureIV2.setImageURI(null)
        captureIV2.setImageURI(imageUri)

        saveImageUriToSharedPreferences(imageUri)
    }

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    private val addMarineLifeDialog: Dialog by lazy {
        Dialog(this).apply {
            setupDialog(R.layout.add_marine_life_dialog)
        }
    }

    private val updateMarineLifeDialog: Dialog by lazy {
        Dialog(this).apply {
            setupDialog(R.layout.update_marine_life_dialog)
        }
    }


    private val loadingDialog: Dialog by lazy {
        Dialog(this).apply {
            setupDialog(R.layout.loading_dialog)
        }
    }

    private val marineLifeViewModel: MarineLifeViewModel by lazy {
        ViewModelProvider(this)[MarineLifeViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Add marineLife start
        val addCloseImg = addMarineLifeDialog.findViewById<ImageView>(R.id.closeImg)
        addCloseImg.setOnClickListener { addMarineLifeDialog.dismiss() }

        val addETedColor = addMarineLifeDialog.findViewById<TextInputEditText>(R.id.edcolor)
        val addETedColorL = addMarineLifeDialog.findViewById<TextInputLayout>(R.id.edcolorL)

        imageUri = retrieveImageUriFromSharedPreferences() ?: createImageUri()

        addETedColor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETedColor, addETedColorL)
            }

        })


        val addETedDepth = addMarineLifeDialog.findViewById<TextInputEditText>(R.id.edDepth)
        val addETedDepthL = addMarineLifeDialog.findViewById<TextInputLayout>(R.id.edDepthL)

        // InputFilter to allow only numeric input for addETDesc
        addETedDepth.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    Toast.makeText(this, "Only numeric input is allowed", Toast.LENGTH_SHORT).show()
                    return@InputFilter ""
                }
            }
            null
        })

        addETedDepth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addETedDepth, addETedDepthL)
            }
        })


        val options = arrayOf("Fish", "Corals")
        val spinner: Spinner = addMarineLifeDialog.findViewById(R.id.spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedOption = options[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle the case where nothing is selected
            }
        }

        imageUri = createImageUri()
        captureIV = addMarineLifeDialog.findViewById(R.id.selectedImageView)
        val captureImgBtn = addMarineLifeDialog.findViewById<Button>(R.id.pickImageButton)
        captureImgBtn.setOnClickListener {
            imageUri = createImageUri()
            contract.launch(imageUri)
        }
        imageUri = createImageUri()
        captureIV2 = updateMarineLifeDialog.findViewById(R.id.upselectedImageView)
        val upCaptureBtn = updateMarineLifeDialog.findViewById<Button>(R.id.updatepickImageButton)

        upCaptureBtn.setOnClickListener {
            imageUri = createImageUri()
            contract.launch(imageUri)
        }
        mainBinding.addMarineLifeFABtn.setOnClickListener {
            clearEditText(addETedColor, addETedColorL)
            clearEditText(addETedDepth, addETedDepthL)
            imageUri = createImageUri()
            captureIV.setImageURI(null) // Clear the ImageView
            addMarineLifeDialog.show()
        }
        val saveMarineLifeBtn = addMarineLifeDialog.findViewById<Button>(R.id.saveBtn)

        saveMarineLifeBtn.setOnClickListener {
            if (validateEditText(addETedColor, addETedColorL) && validateEditText(
                    addETedDepth,
                    addETedDepthL
                )
            ) {
                addMarineLifeDialog.dismiss()

                val newMarineLife = MarineLife(
                    UUID.randomUUID().toString(),
                    addETedColor.text.toString().trim(),
                    Date(), // This line represents the date
                    addETedDepth.text.toString().trim(),
                    imageUri.toString(),
                    typeOfMarine = selectedOption
                )

                marineLifeViewModel.insertMarineLife(newMarineLife).observe(this) {

                    when (it.status) {

                        Status.LOADING -> {
                            loadingDialog.show()

                        }

                        Status.SUCCESS -> {

                            loadingDialog.dismiss()
                            if (it.data?.toInt() != -1) {
                                longToastShow("Added Successfully")

                            }

                        }

                        Status.ERROR -> {

                            loadingDialog.dismiss()
                            it.message?.let { it1 -> longToastShow(it1) }

                        }

                    }
                }

            }
        }


        val updateETTcolor = updateMarineLifeDialog.findViewById<TextInputEditText>(R.id.updateedcolor)
        val updateETcolorL = updateMarineLifeDialog.findViewById<TextInputLayout>(R.id.updateedcolorL)

        updateETTcolor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updateETTcolor, updateETcolorL)
            }

        })

        val updateedDepth = updateMarineLifeDialog.findViewById<TextInputEditText>(R.id.updateedDepth)
        val updateedDepthL = updateMarineLifeDialog.findViewById<TextInputLayout>(R.id.updateedDepthL)

        // InputFilter to allow only numeric input for addETDesc
        updateedDepth.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    Toast.makeText(this, "Only numeric input is allowed", Toast.LENGTH_SHORT).show()
                    return@InputFilter ""
                }
            }
            null
        })
        updateedDepth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(updateedDepth, updateedDepthL)
            }
        })



        val updateCloseImg = updateMarineLifeDialog.findViewById<ImageView>(R.id.updatecloseImg)
        updateCloseImg.setOnClickListener { updateMarineLifeDialog.dismiss() }

        val updateMarineLifeBtn = updateMarineLifeDialog.findViewById<Button>(R.id.updateBtn)

        val updated_spinner: Spinner = updateMarineLifeDialog.findViewById(R.id.spinner)
        updated_spinner.adapter = adapter
        updated_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedOption = options[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle the case where nothing is selected
            }
        }

        val marineLifeViewAdapter = MarineLifeViewAdapter {

                type, position, marineLife ->

            if (type == "delete") {

                marineLifeViewModel
                    .deleteMarineLifeUsingId(marineLife.id).observe(this) {

                        when (it.status) {

                            Status.LOADING -> {
                                loadingDialog.show()

                            }

                            Status.SUCCESS -> {

                                loadingDialog.dismiss()
                                if (it.data != -1) {
                                    longToastShow(" Deleted Successfully")

                                }

                            }

                            Status.ERROR -> {

                                loadingDialog.dismiss()
                                it.message?.let { it1 -> longToastShow(it1) }

                            }

                        }


                    }
            } else if (type == "update") {
                captureIV2.setImageURI(Uri.parse(marineLife.imageUri))
                updateETTcolor.setText(marineLife.color)
                updateedDepth.setText(marineLife.depth)
                imageUri = Uri.parse(marineLife.imageUri)

                updateMarineLifeBtn.setOnClickListener {
                    // Get the new image URI
                    val newImageUri = if (imageUri == null) Uri.parse(marineLife.imageUri) else imageUri

                    if (validateEditText(updateETTcolor, updateETcolorL) && validateEditText(
                            updateedDepth,
                            updateedDepthL
                        )
                    ) {

                        val updateMarineLife = MarineLife(
                            marineLife.id,
                            updateETTcolor.text.toString().trim(),
                            Date(), // This line represents the date
                            updateedDepth.text.toString().trim(),
                            newImageUri.toString(),
                            typeOfMarine = selectedOption

                        )
                        updateMarineLifeDialog.dismiss()
                        loadingDialog.show()
                        marineLifeViewModel.updateMarineLife(updateMarineLife)
                            .observe(this) {

                                when (it.status) {

                                    Status.LOADING -> {
                                        loadingDialog.show()

                                    }

                                    Status.SUCCESS -> {

                                        loadingDialog.dismiss()
                                        if (it.data != -1) {
                                            longToastShow("Updated Successfully")

                                        }

                                    }

                                    Status.ERROR -> {

                                        loadingDialog.dismiss()
                                        it.message?.let { it1 -> longToastShow(it1) }

                                    }

                                }


                            }
                    }

                }

                updateMarineLifeDialog.show()

            }

        }


        mainBinding.marineLifeRV.adapter = marineLifeViewAdapter

        callGetMarineLifeList(marineLifeViewAdapter)
    }

    private fun saveImageUriToSharedPreferences(uri: Uri) {
        val editor = sharedPreferences.edit()
        editor.putString("imageUri", uri.toString())
        editor.apply()
    }

    private fun retrieveImageUriFromSharedPreferences(): Uri? {
        val uriString = sharedPreferences.getString("imageUri", null)
        return uriString?.let { Uri.parse(it) }
    }
    private fun createImageUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "camera_photos_$timeStamp.png"
        val image = File(filesDir, imageFileName)
        return FileProvider.getUriForFile(this, "org.unizd.rma.nekic.FileProvider", image)
    }

    private fun callGetMarineLifeList(marineLifeViewAdapter: MarineLifeViewAdapter) {
        CoroutineScope(Dispatchers.Main).launch {
            marineLifeViewModel.getMarineLifeList().collect {
                when (it.status) {
                    Status.LOADING -> {
                        loadingDialog.show()
                    }
                    Status.SUCCESS -> {
                        it.data?.collect { marineLifeList ->
                            loadingDialog.dismiss()
                            marineLifeViewAdapter.addAllMarineLife(marineLifeList)
                        }
                    }
                    Status.ERROR -> {

                        loadingDialog.dismiss()
                        it.message?.let { it1 -> longToastShow(it1) }
                    }
                }
            }
        }
    }
}