package com.example.praktik12

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.praktik12.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding
    lateinit var mediaHelper : MediaHelper
    lateinit var mhsAdapter : AdapterDataMhs
    lateinit var prodiAdapter : ArrayAdapter<String>
    //menyimpan sementara data yg diperoleh dari Web Service
    var daftarMhs = mutableListOf<HashMap<String,String>> ()
    var daftarProdi = mutableListOf<String>()
    //alamat IP web server dan database server (Laptop/PC)
    //pastikan IP sesuai dengan IP PC
    //dan file PHP sesuai dengan yang tersimpan di XAMPP
    val urlRoot = "http://192.168.0.102"
    val url = "$urlRoot/kampus/show_data.php"
    val url2 = "$urlRoot/kampus/get_nama_prodi.php"
    val url3 = "$urlRoot/kampus/query_upd_del_ins.php"
    var imStr = ""
    var pilihProdi = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate (layoutInflater)
        setContentView (binding.root)
        //inisialisasi adapter RecyclerView
        mhsAdapter = AdapterDataMhs (daftarMhs,this)
        //inisialisasi helper media/gambar
        mediaHelper = MediaHelper(this)
        //mengatur RecyclerView
        binding.listMhs.layoutManager = LinearLayoutManager (this)
        binding.listMhs.adapter = mhsAdapter
        prodiAdapter = ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line, daftarProdi)
        binding.spinProdi.adapter = prodiAdapter
        binding.spinProdi.onItemSelectedListener = itemSelected
        binding.imUpload.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)
        binding.btnInsert.setOnClickListener (this)
        binding.btnDelete.setOnClickListener (this)
        binding.btnFind.setOnClickListener(this)
        //setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onStart() {
        super.onStart()
        showDataMhs("")
        getNamaProdi()
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imUpload -> {
                //membuka gallery
                val intent = Intent()
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(
                    intent, mediaHelper.getRcGallery())
            }
            R.id.btnInsert -> {
                queryInsertUpdateDelete("insert")
            }
            R.id.btnDelete -> {
                queryInsertUpdateDelete("delete")
            }
            R.id.btnUpdate -> {
                queryInsertUpdateDelete("update")
            }
            R.id.btnFind -> {
                showDataMhs(binding.edNamaMhs.text.toString().trim())
            }
        }
    }
    val itemSelected = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            binding.spinProdi.setSelection(0)
            pilihProdi = daftarProdi.get(0)
        }

        override fun onItemSelected(parent: AdapterView<*>?,
                                    view: View?, position: Int, id: Long) {
            //mengambil prodi yang dipilih
            pilihProdi = daftarProdi.get(position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mediaHelper.getRcGallery()) {
                //mengambil gambar dari gallery
                imStr = mediaHelper.getBitmapToString(data!!.data!!,
                    binding.imUpload)
            }
        }
    }

    //penggunaan Volley
    //insert update delete data
    fun queryInsertUpdateDelete(mode: String) {
        val request = object : StringRequest(Method.POST, url3,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val kode = jsonObject.getString("kode")
                if (kode == "000") {
                    Toast.makeText(this, "Operasi berhasil",
                        Toast.LENGTH_LONG).show()
                    showDataMhs("")
                    imStr = ""
                } else {
                    Toast.makeText(this, "Operasi GAGAL",
                        Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server",
                    Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                val alamat = binding.edAlamat.text.toString()
                val jk = if (binding.rbL.isChecked) "Laki-laki" else "Perempuan"
                val nmFile = "IMG" + SimpleDateFormat("yyyyMMddHHmmss",
                    Locale.getDefault()).format(Date()) + ".jpg"

                when (mode) {
                    "insert" -> {
                        hm.put("mode", "insert")
                        hm.put("nim", binding.edNim.text.toString())
                        hm.put("nama", binding.edNamaMhs.text.toString())
                        hm.put("alamat", alamat)
                        hm.put("jk", jk)
                        hm.put("image", imStr)
                        hm.put("file", nmFile)
                        hm.put("nama_prodi", pilihProdi)
                    }
                    "update" -> {
                        hm.put("mode", "update")
                        hm.put("nim", binding.edNim.text.toString())
                        hm.put("nama", binding.edNamaMhs.text.toString())
                        hm.put("alamat", alamat)
                        hm.put("jk", jk)
                        hm.put("image", imStr)
                        hm.put("file", nmFile)
                        hm.put("nama_prodi", pilihProdi)
                    }
                    "delete" -> {
                        hm.put("mode", "delete")
                        hm.put("nim", binding.edNim.text.toString())
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    //mengambil data prodi dari server
    fun getNamaProdi() {
        val request = StringRequest(Request.Method.POST, url2,
            Response.Listener { response ->
                daftarProdi.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length() - 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarProdi.add(jsonObject.getString("nama_prodi"))
                }
                prodiAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error -> })
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    //menampilkan data mahasiswa
    fun showDataMhs(namaMhs: String) {
        val request = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                daftarMhs.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length() - 1)) {
                    val jsonObject = jsonArray.getJSONObject(x)
                    val mhs = HashMap<String, String>()
                    mhs.put("nim", jsonObject.getString("nim"))
                    mhs.put("nama", jsonObject.getString("nama"))
                    mhs.put("nama_prodi", jsonObject.getString("nama_prodi"))
                    mhs.put("alamat", jsonObject.getString("alamat"))
                    mhs.put("jk", jsonObject.getString("jk"))
                    mhs.put("url", jsonObject.getString("url"))
                    daftarMhs.add(mhs)
                }
                mhsAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server",
                    Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                hm.put("nama", namaMhs)
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}