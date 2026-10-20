package com.example.apextime.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.apextime.MainActivity

/**
 * Dispara quando o alarme agendado por agendarLembrete() (LembreteUtil.kt) chega na hora:
 * monta e mostra a notificação de "sua corrida tá começando".
 */
class LembreteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val nomeCorrida = intent.getStringExtra(EXTRA_NOME) ?: "Sua corrida"
        val idUnico = intent.getIntExtra(EXTRA_ID, 0)

        criarCanalDeNotificacao(context)

        val intentAbrirApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            idUnico,
            intentAbrirApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("ApexTime")
            .setContentText("$nomeCorrida está prestes a começar!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // No Android 13+ a notificação só pode ser mostrada se o usuário aceitou a permissão.
        // Se ele negou, simplesmente não mostramos nada.
        val temPermissao = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (temPermissao) {
            NotificationManagerCompat.from(context).notify(idUnico, notificacao)
        }
    }

    companion object {
        const val CANAL_ID = "lembretes_corrida"
        const val EXTRA_NOME = "extra_nome_corrida"
        const val EXTRA_ID = "extra_id_lembrete"

        fun criarCanalDeNotificacao(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val canal = NotificationChannel(
                    CANAL_ID,
                    "Lembretes de corrida",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Avisa um pouco antes de uma corrida marcada começar"
                }
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(canal)
            }
        }
    }
}