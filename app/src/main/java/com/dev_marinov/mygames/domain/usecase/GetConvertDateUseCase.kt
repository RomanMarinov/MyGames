package com.dev_marinov.mygames.domain.usecase

import com.dev_marinov.mygames.data.ObjectConvertDate
import com.dev_marinov.mygames.domain.repository.InterFaceConvertDateRepository

// класс переработки полученной даты календаря (где нет нулей, там поставил)
class GetConvertDateUseCase(private val interFaceConvertDateRepository: InterFaceConvertDateRepository) {

    fun execute(objectConvertDate: ObjectConvertDate): String {
        return interFaceConvertDateRepository.getName(objectConvertDate)
    }
}