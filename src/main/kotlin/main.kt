import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.roundToInt

fun main(args: Array<String>) {


    exampleOf("just") {
        val observable = Observable.just(listOf(1))
    }

    exampleOf("fromIterable") {
        val observable = Observable.fromIterable(listOf(1,2,3))
    }

    exampleOf("subscribe") {
        val observable = Observable.just(1, 2, 3)
        observable.subscribe { println(it) }
    }

    exampleOf("empty") {
        val observable = Observable.empty<Unit>()
        observable.subscribeBy(
                onNext = { println(it) },
                onComplete = { println("Completed") }
        )
    }

    exampleOf("never") {
        val subscriptions = CompositeDisposable()
        val observable = Observable.never<Unit>()
                .doOnSubscribe { println("never example has been subscribed") }
                .doOnDispose { println("never example has been disposed") }
                .subscribeBy(
                onNext = { println(it) },
                onComplete = { println("Completed") }
                )

        subscriptions.add(observable)
        subscriptions.dispose()
    }

    exampleOf("range") {
        val observable = Observable.range(1, 10)

        observable.subscribe {
            val n = it.toDouble()
            val fibonacci = ((Math.pow(1.61803, n) - Math.pow(0.61803, n)) / 2.23606).roundToInt()
            println(fibonacci)
        }
    }

    exampleOf("dispose") {
        val mostPopular: Observable<String> = Observable.just("A", "B", "C")

        val subscription = mostPopular.subscribe {
            println(it)
        }

        subscription.dispose()
    }

    exampleOf("composite disposable") {
        val subscriptions = CompositeDisposable()

        val disposable = Observable.just("A", "B", "C")
                .subscribe {
                    println(it)
                }

        subscriptions.add(disposable)
    }

    exampleOf("create") {
        val disposables = CompositeDisposable()

        Observable.create<String> { emitter ->



        }
    }

    exampleOf("create") {
        val disposables = CompositeDisposable()

        val observable = Observable.create<String> { emitter ->
            emitter.onNext("Primeiro dato emitido")

            emitter.onError(Throwable("Emitter emitiu um erro"))

            emitter.onComplete() //Nunca é chamado pois o sequencia terminou com erro

            emitter.onNext("Segundo evento emitido") //Também não é chamado pois a sequencia já terminou


        }.subscribeBy(
                onNext = { println(it) },
                onComplete = { println("completed") },
                onError = { println("error: ${it.message}") }
        )

        disposables.add(observable)
    }

    exampleOf("defer") {
        val disposables = CompositeDisposable()

        var flip = false

        val factory: Observable<Int> = Observable.defer {

            flip = !flip

            if (flip) {
                Observable.just(1, 2, 3)
            } else {
                Observable.just(4,5,6)
            }
        }

        for (i in 0 until 4) {
            disposables.add(factory.subscribe {
                println(it)
            })
        }

        disposables.dispose()
    }

    exampleOf("single") {
        val subscriptions = CompositeDisposable()

        fun loadText(fileName: String): Single<String> {
            return Single.create create@{ emitter ->
                val file = File(fileName)

                if (!file.exists()) {
                    emitter.onError(FileNotFoundException("There is no such file"))
                    return@create
                }

                val contents = file.readText(Charsets.UTF_8)

                emitter.onSuccess(contents)
            }
        }

        subscriptions.add(loadText("Copyright.txt").subscribeBy(
                onSuccess = { println(it) },
                onError = { println("Error, $it")}
        ))

        subscriptions.dispose()


    }


}