package com.example.apextime.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.Instant

/** Quanto antes da largada a notificação deve disparar. */
private const val ANTECEDENCIA_MINUTOS = 30L

/**
 * Agenda uma notificação local para disparar um pouco antes da corrida começar.
 *
 * Usa setAndAllowWhileIdle (alarme "inexato") em vez de setExactAndAllowWhileIdle
 * de propósito: alarme exato no Android 12+ exige uma permissão especial
 * (SCHEDULE_EXACT_ALARM) que só é concedida numa tela separada do sistema,
 * o que complicaria demais pra um lembrete que não precisa ser no segundo exato.
 * Com o inexato, o disparo pode atrasar alguns minutos se o aparelho estiver
 * em economia de bateria, o que é um compromisso aceitável aqui.
 *
 * Retorna false (e não agenda nada) se a corrida já vai começar a qualquer
 * momento ou já passou.
 */
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