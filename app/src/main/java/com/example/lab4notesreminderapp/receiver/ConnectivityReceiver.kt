
/**
 * BroadcastReceiver that listens for connectivity changes.
 */
package com.example.lab4notesreminderapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "Connectivity changed", Toast.LENGTH_SHORT).show()
    }
}
