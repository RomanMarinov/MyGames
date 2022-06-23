package com.dev_marinov.mygames.domain.repository

import com.dev_marinov.mygames.data.ObjectConvertDate

interface InterFaceConvertDateRepository {

    fun getName(objectConvertDate: ObjectConvertDate) : String
}