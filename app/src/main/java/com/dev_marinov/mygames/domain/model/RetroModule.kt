package com.dev_marinov.mygames.domain.model

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// модули, умеют предоставлять требуемые объекты. @Module - мы сообщили дагееру что скласс является модулем
// Именно в модулях мы и пишем весь код по созданию объектов. Это обычные классы, но с парой аннотаций.
@Module
class RetroModule {
    //https://api.rawg.io/api/games?key=YOUR_API_KEY&dates=2019-09-01,2019-09-30&platforms=18,1,7

    private val baseUrl = "https://api.rawg.io/api/"

    // функия которая будет возращаться как инструкция интерфейса retroService
    @Singleton
    @Provides
    fun getRetroServiceInterFace(retrofit: Retrofit): RetroServiceInterFace {
        return retrofit.create(RetroServiceInterFace::class.java)
    }

    // функция по возрату экземпляра retrofit
    // @Singleton(и любая другая аннотация области действия) делает ваш класс единственным экземпляром в
    // вашем графе зависимостей (это означает, что этот экземпляр будет «одиночным», пока существует объект
    // Component).
    // Каждый раз, когда вы вводите @Singleton аннотированный класс (с @Inject аннотацией), это
    // будет один и тот же экземпляр, если вы вводите его из одного и того же компонента.
    // Единственный экземпляр класса, уникальный для определенного компонента , доступ к которому
    // ограничен областью действия компонента. Цель синглтона Предоставить один экземпляр класса в
    // графе зависимостей (компоненте). компонент обычно инициализируется на уровне приложения,
    // поскольку он выполняет только один за все время жизни приложения и доступен для всех действий и фрагментов.

    // когда мы объявляем эту функцию как Provides мы можем вызывать ее к где хотим
    @Singleton
    @Provides
    fun getRetrofitInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()) // конвертер Gson
            .build()
    }
}