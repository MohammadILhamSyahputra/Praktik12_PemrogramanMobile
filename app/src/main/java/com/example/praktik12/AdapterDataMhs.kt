package com.example.praktik12

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.praktik12.databinding.RowMhsBinding
import com.squareup.picasso.Picasso

class AdapterDataMhs (
    val dataMhs : List<HashMap<String, String>>,
    val mainActivity: MainActivity) :
    RecyclerView.Adapter<AdapterDataMhs.HolderDataMhs>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HolderDataMhs {
            //menghubungkan layout row_mhs.xml ke RecyclerView
            val binding = RowMhsBinding.inflate(
                LayoutInflater.from(p0.context), p0, false
            )
            return HolderDataMhs(binding)
        }

        override fun getItemCount(): Int {
            //jumlah data pada RecyclerView
            return dataMhs.size
        }

        override fun onBindViewHolder(p0: HolderDataMhs, p1: Int) {
            //mengambil data berdasarkan posisi
            val data = dataMhs[p1]
            //menampilkan data ke TextView
            p0.binding.txNim.text = data["nim"]
            p0.binding.txNama.text = data["nama"]
            p0.binding.txProdi.text = data["nama_prodi"]
            p0.binding.txAlamat.text = data["alamat"]
            p0.binding.txJK.text = data["jk"]

            if (p1.rem(2) == 0) p0.binding.cLayout.setBackgroundColor(
                Color.rgb(230, 245, 240)
            )
            else p0.binding.cLayout.setBackgroundColor(
                Color.rgb(255, 255, 245)
            )

            //event klik item RecyclerView
            p0.binding.cLayout.setOnClickListener {
                //mencari posisi prodi pada Spinner
                val pos = mainActivity.daftarProdi.indexOf(
                    data["nama_prodi"]
                )
                //menampilkan data ke MainActivity
                mainActivity.binding.spinProdi.setSelection(pos)
                mainActivity.binding.edNim.setText(data["nim"])
                mainActivity.binding.edNamaMhs.setText(data["nama"])
                mainActivity.binding.edAlamat.setText(data["alamat"])
                if (data["jk"] == "Laki-laki") mainActivity.binding.rbL.isChecked = true
                else mainActivity.binding.rbP.isChecked = true
                //menampilkan gambar
                Picasso.get().load(data["url"])
                    .into(mainActivity.binding.imUpload)
            }

            //menampilkan foto mahasiswa pada RecyclerView
            if (data["url"] != "") {
                Picasso.get().load(data["url"]).into(p0.binding.imageView)
            }
        }

        inner class HolderDataMhs(val binding: RowMhsBinding) :
            RecyclerView.ViewHolder(binding.root)
    }