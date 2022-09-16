package com.chahye.quizlocker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chahye.quizlocker.R
import kotlinx.android.synthetic.main.activity_file_ex.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

class FileExActivity : AppCompatActivity() {

    val filename = "data.txt"

    val MY_PERMISSION_REQUEST = 999 // 권한 요청 코드
    var granted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_ex)

        checkPermission()

        saveButton.setOnClickListener {
            val text = textField.text.toString()
            when {
                TextUtils.isEmpty(text) -> {
                    Toast.makeText(applicationContext, "텍스트가 비어있습니다.", Toast.LENGTH_LONG).show()
                }
                !isExternalStorageWritable() -> {
                    Toast.makeText(applicationContext, "외부 저장장치가 없습니다.", Toast.LENGTH_LONG).show()
                }
                else -> {
                    // 내부 저장소 파일에 저장하는 함수 호출
                    //saveToInnerStorage(text, filename)

                    // 외부 저장소 파일에 저장하는 함수 호출
                    //saveToExternalStorage(text, filename)

                    // 외부 저장소 "/sdcard/data.txt"에 데이터를 저장
                    saveToExternalCustomDirectory(text)
                }
            }
        }

        loadButton.setOnClickListener {
            try {
                //textField.setText(loadFromInnerStorage(filename))

                // 외부 저장소 앱전용 디렉토리의 파일에서 읽어온 데이터로 textField의 텍스트를 설정
                //textField.setText(loadFromExternalStorage(filename))

                // 외부 저장소 "/sdcard/data.txt"에서 데이터를 불러온다
                textField.setText(loadFromExternalCustomDirectory())
            } catch (e: FileNotFoundException) {
                Toast.makeText(applicationContext, "저장된 텍스트가 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun checkPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this@FileExActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        when {
            permissionCheck != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this@FileExActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST
                )
            }
        }
    }

    // 권한요청 결과 콜백
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSION_REQUEST -> {
                when {
                    grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        granted = true
                    }
                    else -> {
                        granted = false
                    }
                }
            }
        }
    }

    // 임의의 경로에 파일에 데이터를 저장하는 함수
    fun saveToExternalCustomDirectory(text: String, filepath: String = "/sdcard/data.txt") {
        when {
            granted -> {
                val fileOutputStream = FileOutputStream(File(filepath))
                fileOutputStream.write(text.toByteArray())
                fileOutputStream.close()
            }
            else -> {
                Toast.makeText(applicationContext, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 임의의 경로에 파일에서 데이터를 읽는 함수
    private fun loadFromExternalCustomDirectory(filepath: String = "/sdcard/data.txt"): String {
        when {
            granted -> {
                return FileInputStream(File(filepath)).reader().readText()
            }
            else -> {
                Toast.makeText(applicationContext, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                return ""
            }
        }
    }

    fun saveToInnerStorage(text: String, filename: String) {
        val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
    }

    fun loadFromInnerStorage(filename: String): String {
        val fileInputStream = openFileInput(filename)
        return fileInputStream.reader().readText()
    }

    // 외부 저장장치를 사용할 수 있고 쓸 수 있는지 체크하는 함수
    fun isExternalStorageWritable(): Boolean {
        when {
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED -> return true
            else -> return false
        }
    }

    // 외부 저장장치에서 앱 전용데이터로 사용할 파일 객체를 반환하는 함수
    fun getAppDataFileFormExternalStorage(filename: String): File {
        // KITKAT 버전부터는 앱전용 디렉토리의 디렉토리 타입 상수를 지원
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        } else {
            File(Environment.getExternalStorageDirectory().absolutePath + "/Documents")
        }

        dir?.mkdirs()
        return File("${dir?.absolutePath}${File.separator}${filename}")
    }

    // 외부저장소 앱 전용 디렉토리에 파일로 저장하는 함수
    fun saveToExternalStorage(text: String, filename: String) {
        val fileOutputStream = FileOutputStream(getAppDataFileFormExternalStorage(filename))
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
    }

    // 외부저장소 앱 전용 디렉토리에서 파일 데이터를 불러오는 함수
    fun loadFromExternalStorage(filename: String): String {
        return FileInputStream(getAppDataFileFormExternalStorage(filename)).reader().readText()
    }

}