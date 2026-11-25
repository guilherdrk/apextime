package com.example.apextime.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.Instant

private const val ANTECEDENCIA_MINUTOS = 30L

fun agendarLembrete(
    context: Context,
    idUnico: Int,
    nomeCorrida: String,
    instanteCorrida: Instant
): Boolean {
    val horarioDoAlarme = instanteCorrida.toEpochMilli() - (ANTECEDENCIA_MINUTOS * 60_000)
    if (horarioDoAlarme <= System.currentTimeMillis()) {
        return false
    }

    LembreteReceiver.criarCanalDeNotificacao(context)

    val intent = Intent(context, LembreteReceiver::class.java).apply {
        putExtra(LembreteReceiver.EXTRA_NOME, nomeCorrida)
        putExtra(LembreteReceiver.EXTRA_ID, idUnico)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        idUnico,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, horarioDoAlarme, pendingIntent)
    return true
}
