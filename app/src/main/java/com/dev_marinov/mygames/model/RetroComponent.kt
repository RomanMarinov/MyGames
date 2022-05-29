package com.dev_marinov.mygames.model


import com.dev_marinov.mygames.presentation.ViewModelListGames
import dagger.Component
import javax.inject.Singleton

// @Component() аннотация используется для интерфейса, который объединит все части процесса внедрения зависимостей.
// При использовании данной аннотации мы определяем из каких модулей или других компонентов будут браться зависимости.
// Также здесь можно определить, какие зависимости будут видны открыто (могут быть внедрены) и где компонент может внедрять объекты.
// @Component, в общем, что-то вроде моста между @Module (рассмотрим эту аннотацию позже) и @Inject.

// тут мы определяем где и в каких классах мы хотим получить доступ к нашему модулю
// в Component() мы передадим модули
@Singleton
@Component(modules = [RetroModule::class])
interface RetroComponent {

    // определим к чему мы хотим получить доступ. Мы хотим получить доступ к нашему модулю внутри
    // нашей модели представления и мы определим нашу модель ViewModelMainActivity представления внутри компонента
        fun inject(viewModelListGames: ViewModelListGames)


}