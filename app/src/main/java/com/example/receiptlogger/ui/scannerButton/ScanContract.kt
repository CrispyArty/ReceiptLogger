package com.example.receiptlogger.ui.scannerButton

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.example.receiptlogger.Intents
import com.example.receiptlogger.ScannerActivity

class QrCodeScanContract : ActivityResultContract<Unit, String?>() {
    override fun createIntent(context: Context, input: Unit): Intent =
        Intent(context, ScannerActivity::class.java).setAction(Intents.SCAN_INTENT)

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): String? {
        if (resultCode != Activity.RESULT_OK) {
            return null;
        }

        return intent?.getStringExtra(Intents.Result.TEXT)
    }
}