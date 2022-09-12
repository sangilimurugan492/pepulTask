package com.task.pepultask.data.ui.main.view

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.pepultask.R
import com.task.pepultask.data.api.ApiHelper
import com.task.pepultask.data.api.RetrofitBuilder
import com.task.pepultask.data.model.request.DeleteRequest
import com.task.pepultask.data.model.request.SelectRequest
import com.task.pepultask.data.model.request.UploadRequest
import com.task.pepultask.data.model.response.Data
import com.task.pepultask.data.model.response.FileUploadSuccess
import com.task.pepultask.data.model.response.SelectSuccess
import com.task.pepultask.data.ui.base.ViewModelFactory
import com.task.pepultask.data.ui.main.MainViewModel
import com.task.pepultask.data.ui.main.adapter.ListAdapter
import com.task.pepultask.data.ui.main.adapter.PaginationScrollListener
import com.task.pepultask.utils.Status
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ListActivity : AppCompatActivity(), ListAdapter.ListClickListner {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ListAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar
    private var lastFetchId : String = ""
    private var prevFetchId : String = ""
    public var isLoading = false
    private var isLastPage = false
    private var PICK_IMAGE_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)

        setupViewModel()
        setupUI()
        setupObservers(SelectRequest(lastFetchId))
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ListAdapter(this, arrayListOf())
        recyclerView.adapter = adapter
        val manager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : PaginationScrollListener(manager){
            override fun loadMoreItems() {
                this.isLoading = ListActivity().isLoading
                if (!prevFetchId.equals(lastFetchId)) {
                    prevFetchId = lastFetchId
                    setupObservers(SelectRequest(lastFetchId))
                }
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false

        })
    }

    private fun setupObservers(selectRequest: SelectRequest) {
        viewModel.getDetails(selectRequest).observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        recyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        resource.data?.let {
                            if ("".equals(lastFetchId))
                                retrieveList(it)
                            else
                                retrieveNextPage(it)
                            Log.d("Response", "setupObservers: $it")
                        }
                    }
                    Status.ERROR -> {
                        recyclerView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun retrieveList(selectSuccess: SelectSuccess) {
        adapter.apply {
            val contents : List<Data> = selectSuccess.data
            if (contents.isNotEmpty()) {
                addContents(contents)
                prevFetchId = lastFetchId
                lastFetchId = contents.get(contents.size - 1).id.toString()
                if (contents.get(contents.size - 1).id >  1) adapter.addLoadingFooter() else isLastPage = true
                notifyDataSetChanged()
            } else
                Toast.makeText(applicationContext, "No Content available according your search", Toast.LENGTH_LONG).show()
        }
    }

    private fun retrieveNextPage(selectSuccess: SelectSuccess) {
        adapter.apply {
            removeLoadingFooter()
            isLoading = false
            val contents : List<Data> = selectSuccess.data
            addAll(contents)
            prevFetchId = lastFetchId
            lastFetchId = contents.get(contents.size - 1).id.toString()
            if (!prevFetchId.equals(lastFetchId)) {
                if (contents.get(contents.size - 1).id >  1) {
                    adapter.addLoadingFooter()
                    isLoading = true
                } else isLastPage = true
            }
        }
    }

    override fun onItemClicked(selectedItem: Data) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("ImageUrl", selectedItem.file)
        startActivity(intent)
    }

    override fun onItemLongClicked(selectedItemId: String) {

        viewModel.deleteDetails(DeleteRequest(selectedItemId)).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.visibility = View.GONE
                        it.data?.enqueue(object : Callback<FileUploadSuccess?> {
                            override fun onResponse(
                                call: Call<FileUploadSuccess?>,
                                response: Response<FileUploadSuccess?>
                            ) {
                                Log.d("Response:", "onResponse: " + response.body()?.result)
                                if (response.body()?.statusCode != null && response.body()?.statusCode == 200) {
                                    Toast.makeText(
                                        applicationContext,
                                        "User Deleted Successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "User Already Deleted",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<FileUploadSuccess?>, t: Throwable) {
                                Toast.makeText(
                                    applicationContext,
                                    t.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    }

                    Status.ERROR -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.equals(R.id.action_image)) {
            launchGallery()
        } else {
            launchGallery()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST){
            val file: File = File(data?.getData()?.path!!)
            val requestFile: RequestBody = file.asRequestBody()
            val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)

            viewModel.uploadDetails(multipartBody, UploadRequest(data.data?.path.toString())).observe(this) { it ->
                it?.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            progressBar.visibility = View.GONE
                            it.data?.enqueue(object : Callback<FileUploadSuccess?> {
                                override fun onResponse(
                                    call: Call<FileUploadSuccess?>,
                                    response: Response<FileUploadSuccess?>
                                ) {
                                    Log.d("Response:", "onResponse: " + response.body()?.result)
                                    Toast.makeText(
                                        applicationContext,
                                        response.body()?.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                override fun onFailure(call: Call<FileUploadSuccess?>, t: Throwable) {
                                    Log.d("Response:", "onResponse: " + t.message)
                                    Toast.makeText(
                                        applicationContext,
                                        t.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            })
                        }
                        Status.ERROR -> {
                            Log.d("Response:", "onResponse: " + it.message)
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.LOADING -> {
                            progressBar.visibility = View.VISIBLE
                        }
                    }

                }
            }
        }
    }

    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

}