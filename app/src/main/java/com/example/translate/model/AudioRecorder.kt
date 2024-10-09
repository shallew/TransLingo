package com.example.translate.model

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import com.example.translate.tools.GlobalApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *   文件名: com.example.translate.model.AudioRecordANdToString
 *   @author: shallew
 *   @data: 2024/7/27 21:45
 *   @about: TODO
 */
object AudioRecorder {

    private const val sampleRate = 16000 // 或者 8000
    private const val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private const val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private val pcmFilePath: String =
        GlobalApplication.context.getExternalFilesDir(null)?.absolutePath + "/recording.pcm"
    lateinit var pcmFile: File
    private lateinit var audioRecord: AudioRecord

    @Volatile
    private var isRecording = false

    @SuppressLint("MissingPermission")
    suspend fun startRecording() = withContext(Dispatchers.IO) {
//        Log.d("RecordingDebug", "startRecording:Model开始录音")

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )
        audioRecord?.startRecording()
        isRecording = true

        val buffer = ByteArray(minBufferSize)
        pcmFile = File(pcmFilePath)

        val outputStream = ByteArrayOutputStream()
        while (isRecording) {
            val read = audioRecord?.read(buffer, 0, minBufferSize) ?: 0
            if (read > 0) {
                outputStream.write(buffer, 0, read)
                saveAsWav(outputStream.toByteArray(), pcmFile)
            }
        }
    }
    private fun saveAsWav(pcmData: ByteArray, wavFile: File) {
        val sampleRate = 44100
        val totalAudioLen = pcmData.size.toLong()
        val totalDataLen = totalAudioLen + 36
        val byteRate = 16 * sampleRate * 1 / 8

        val header = byteArrayOf(
            'R'.code.toByte(), 'I'.code.toByte(), 'F'.code.toByte(), 'F'.code.toByte(),
            (totalDataLen and 0xff).toByte(),
            (totalDataLen shr 8 and 0xff).toByte(),
            (totalDataLen shr 16 and 0xff).toByte(),
            (totalDataLen shr 24 and 0xff).toByte(),
            'W'.code.toByte(), 'A'.code.toByte(), 'V'.code.toByte(), 'E'.code.toByte(),
            'f'.code.toByte(), 'm'.code.toByte(), 't'.code.toByte(), ' '.code.toByte(),
            16, 0, 0, 0,
            1, 0,
            1, 0,
            (sampleRate and 0xff).toByte(),
            (sampleRate shr 8 and 0xff).toByte(),
            (sampleRate shr 16 and 0xff).toByte(),
            (sampleRate shr 24 and 0xff).toByte(),
            (byteRate and 0xff).toByte(),
            (byteRate shr 8 and 0xff).toByte(),
            (byteRate shr 16 and 0xff).toByte(),
            (byteRate shr 24 and 0xff).toByte(),
            (1 * 16 / 8).toByte(),
            0,
            16, 0,
            'd'.code.toByte(), 'a'.code.toByte(), 't'.code.toByte(), 'a'.code.toByte(),
            (totalAudioLen and 0xff).toByte(),
            (totalAudioLen shr 8 and 0xff).toByte(),
            (totalAudioLen shr 16 and 0xff).toByte(),
            (totalAudioLen shr 24 and 0xff).toByte()
        )

        try {
            val fos = FileOutputStream(wavFile)
            fos.write(header)
            fos.write(pcmData)
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    suspend fun stopRecording(): String? {
        isRecording = false
        audioRecord.stop()
        audioRecord.release()
        return if (pcmFilePath != "") convertPcmToBase64() else null
    }

    private suspend fun convertPcmToBase64(): String = withContext(Dispatchers.IO) {
        val file = File(pcmFilePath)
        val fileContent = file.readBytes()
        Log.d("RecordingDebug", "convertPcmToBase64: ")
        return@withContext Base64.encodeToString(fileContent, Base64.NO_WRAP)
    }
}