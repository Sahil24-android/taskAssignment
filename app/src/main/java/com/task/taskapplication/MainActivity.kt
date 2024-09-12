package com.task.taskapplication

import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textview.MaterialTextView
import com.task.taskapplication.adapter.UserAdapter
import com.task.taskapplication.databinding.ActivityMainBinding
import com.task.taskapplication.databinding.UploadImageDialogBinding
import com.task.taskapplication.repository.UserRepository
import com.task.taskapplication.room.database.UserDatabase
import com.task.taskapplication.room.entity.UserEntity
import com.task.taskapplication.viewmodel.UserViewModel
import com.task.taskapplication.viewmodel.UserViewModelFactory
import java.util.Locale

class MainActivity : AppCompatActivity(), UserAdapter.OnClick {
    private lateinit var userViewModel: UserViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val userDao = UserDatabase.getDatabase(this).userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModelFactory(repository)

        // Create ViewModel using the factory
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLocation()

        userAdapter = UserAdapter(this)
        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = userAdapter

        userViewModel.fetchUsers()

        userViewModel.users.observe(this) { users ->
            userAdapter.updateList(users)
        }

        userViewModel.error.observe(this) { errorMessage ->

        }


    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Display location details on the screen
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude

                    // Fetch and display the address using latitude and longitude
                    fetchAddressFromLocation(latitude, longitude)
                    // Handle location display logic
                }
            }
        }
    }

    private fun fetchAddressFromLocation(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressText = address.getAddressLine(0) // Get the first address line

                // Display the address in the TextView
                binding.currentAddress.text = "Location: $addressText"
            } else {
                binding.currentAddress.text = "Unable to fetch address"
            }
        } catch (e: Exception) {
            binding.currentAddress.text = "Error fetching address"
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getLocation()
            }
        }

    override fun onUploadClick(user: UserEntity) {
        showCustomDialog(user)
    }

    private var dialogImageView: ImageView? = null
    private var chooseImage:MaterialTextView? = null
    private fun showCustomDialog(user: UserEntity) {
        val dialog = Dialog(this)
        val view = UploadImageDialogBinding.inflate(layoutInflater)

        dialog.setContentView(view.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Make the dialog background transparent if needed
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_shape_background) // Set the custom background with rounded corners

        dialogImageView = view.imageView
        chooseImage = view.chooseImage


        view.close.setOnClickListener {
            dialog.dismiss()
        }

        view.imageView.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    // Optional cropping
                .compress(1024)            // Optional compression, max size in KB
                .maxResultSize(1080, 1080) // Optional resolution
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        view.chooseImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    // Optional cropping
                .compress(1024)            // Optional compression, max size in KB
                .maxResultSize(1080, 1080) // Optional resolution
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        view.uploadButton.setOnClickListener {
            if (imageUri!=null){
               userViewModel.updateUserImage(user.id,imageUri.toString())
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private var imageUri: Uri? = null
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                imageUri = data?.data!!

                //mProfileUri = fileUri
                dialogImageView?.setImageURI(imageUri)
                if (imageUri==null){
                    Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show()
                    chooseImage?.visibility= View.VISIBLE
                }else{
                    chooseImage?.visibility= View.GONE
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()

            }
        }
}