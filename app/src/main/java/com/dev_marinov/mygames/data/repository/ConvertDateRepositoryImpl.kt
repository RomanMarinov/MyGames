package com.dev_marinov.mygames.data.repository

import com.dev_marinov.mygames.data.ObjectConvertDate
import com.dev_marinov.mygames.domain.repository.InterFaceConvertDateRepository


// УБРАТЬ МАНИПУЛЯЦИИ С ДАННЫМИ
class ConvertDateRepositoryImpl() : InterFaceConvertDateRepository {

    override fun getName(objectConvertDate: ObjectConvertDate) : String {

        var dateResult = ""

        // i1 - год, i2 - месяц(январь с нуля идет отсчет) ПЛЮС ОДИН, i3 - день
        val i1 = objectConvertDate.year
        val i2 = objectConvertDate.month + 1
        val i3 = objectConvertDate.day

        // если месяц не входит в диапазон от 10 до 12, а день входит от 10 до 31
        if (i2 !in 10..12 && i3 in 10..31) dateResult = String.format("$i1" + "-" + "0$i2" + "-" + "$i3")
        // если месяц входит в диапазон от 10 до 12, а день не входит от 10 до 31
        if (i2 in 10..12 && i3 !in 10..31) dateResult = String.format("$i1" + "-" + "$i2" + "-" + "0$i3")
        // если месяц входит в диапазон от 10 до 12, и день входит от 10 до 31
        if (i2 in 10..12 && i3 in 10..31) dateResult = String.format("$i1" + "-" + "$i2" + "-" + "$i3")
        // если месяц не входит в диапазон от 10 до 12, и день не входит от 10 до 31
        if (i2 !in 10..12 && i3 !in 10..31) dateResult = String.format("$i1" + "-" + "0$i2" + "-" + "0$i3")

        return dateResult

    }

}