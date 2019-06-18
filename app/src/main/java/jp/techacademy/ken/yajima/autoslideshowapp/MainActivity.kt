package jp.techacademy.ken.yajima.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? =null
    private var mHandler=Handler()

    val URIs= arrayListOf<Uri>()
    var a=0
    var count=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        next_button.setOnClickListener {
            displayNext()
        }
        back_button.setOnClickListener {
            displayBack()
        }
        start_stop_button.setOnClickListener {
            count++
            if(count%2==1) displayAuto()
            else stop()
        }
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentInfo()
                }
        }
    }
    private fun getContentInfo(){
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )
        if (cursor.moveToFirst()) {
            do{
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                URIs.add(imageUri)

                imageView.setImageURI(URIs[a])
                Log.d("ANDROID", "URI:" + imageUri.toString())
            }while(cursor.moveToNext())
        }
        cursor.close()

    }
    private fun displayNext(){
        a++
        if(a>=URIs.size) a=0
        imageView.setImageURI(URIs[a])
    }
    private fun displayBack(){
        if(a==0){
            a=URIs.size-1
        } else a--
        imageView.setImageURI(URIs[a])
    }
    private fun displayAuto(){
        mTimer=Timer()

        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    displayNext()
                }
            }
        }, 2000, 2000)
    }
    private fun stop(){
        if(mTimer != null){
            mTimer!!.cancel()
            mTimer=null
        }
    }
}
