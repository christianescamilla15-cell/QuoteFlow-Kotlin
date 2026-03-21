package com.christianhernandez.quoteflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Quote::class], version = 1, exportSchema = false)
abstract class QuoteDatabase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var INSTANCE: QuoteDatabase? = null

        fun getDatabase(context: Context): QuoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuoteDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .addCallback(SeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedDatabase(database.quoteDao())
                }
            }
        }
    }
}

suspend fun seedDatabase(dao: QuoteDao) {
    val quotes = listOf(
        // --- STOICISM (English) ---
        Quote(id = "s01", text = "The happiness of your life depends upon the quality of your thoughts.", author = "Marcus Aurelius", category = "stoicism", lang = "en"),
        Quote(id = "s02", text = "You have power over your mind, not outside events. Realize this, and you will find strength.", author = "Marcus Aurelius", category = "stoicism", lang = "en"),
        Quote(id = "s03", text = "Waste no more time arguing about what a good man should be. Be one.", author = "Marcus Aurelius", category = "stoicism", lang = "en"),
        Quote(id = "s04", text = "It is not that we have a short time to live, but that we waste a great deal of it.", author = "Seneca", category = "stoicism", lang = "en"),
        Quote(id = "s05", text = "We suffer more often in imagination than in reality.", author = "Seneca", category = "stoicism", lang = "en"),
        Quote(id = "s06", text = "Man is not worried by real problems so much as by his imagined anxieties about real problems.", author = "Epictetus", category = "stoicism", lang = "en"),
        Quote(id = "s07", text = "First say to yourself what you would be; and then do what you have to do.", author = "Epictetus", category = "stoicism", lang = "en"),
        Quote(id = "s08", text = "The best revenge is not to be like your enemy.", author = "Marcus Aurelius", category = "stoicism", lang = "en"),
        Quote(id = "s09", text = "Difficulties strengthen the mind, as labor does the body.", author = "Seneca", category = "stoicism", lang = "en"),
        Quote(id = "s10", text = "He who fears death will never do anything worthy of a living man.", author = "Seneca", category = "stoicism", lang = "en"),
        Quote(id = "s11", text = "No man is free who is not master of himself.", author = "Epictetus", category = "stoicism", lang = "en"),
        Quote(id = "s12", text = "The soul becomes dyed with the color of its thoughts.", author = "Marcus Aurelius", category = "stoicism", lang = "en"),
        Quote(id = "s13", text = "If it is not right, do not do it; if it is not true, do not say it.", author = "Marcus Aurelius", category = "stoicism", lang = "en"),

        // --- STOICISM (Spanish) ---
        Quote(id = "s14", text = "La felicidad de tu vida depende de la calidad de tus pensamientos.", author = "Marco Aurelio", category = "stoicism", lang = "es"),
        Quote(id = "s15", text = "Tienes poder sobre tu mente, no sobre los eventos externos. Date cuenta de esto y encontraras fortaleza.", author = "Marco Aurelio", category = "stoicism", lang = "es"),
        Quote(id = "s16", text = "No es que tengamos poco tiempo de vida, sino que desperdiciamos mucho de el.", author = "Seneca", category = "stoicism", lang = "es"),
        Quote(id = "s17", text = "Sufrimos mas en la imaginacion que en la realidad.", author = "Seneca", category = "stoicism", lang = "es"),
        Quote(id = "s18", text = "Primero di lo que quieres ser; y luego haz lo que tengas que hacer.", author = "Epicteto", category = "stoicism", lang = "es"),
        Quote(id = "s19", text = "Las dificultades fortalecen la mente, como el trabajo fortalece el cuerpo.", author = "Seneca", category = "stoicism", lang = "es"),
        Quote(id = "s20", text = "Ningun hombre es libre si no es dueno de si mismo.", author = "Epicteto", category = "stoicism", lang = "es"),

        // --- PHILOSOPHY (English) ---
        Quote(id = "p01", text = "The unexamined life is not worth living.", author = "Socrates", category = "philosophy", lang = "en"),
        Quote(id = "p02", text = "I think, therefore I am.", author = "Rene Descartes", category = "philosophy", lang = "en"),
        Quote(id = "p03", text = "One cannot step twice in the same river.", author = "Heraclitus", category = "philosophy", lang = "en"),
        Quote(id = "p04", text = "The only true wisdom is in knowing you know nothing.", author = "Socrates", category = "philosophy", lang = "en"),
        Quote(id = "p05", text = "He who has a why to live can bear almost any how.", author = "Friedrich Nietzsche", category = "philosophy", lang = "en"),
        Quote(id = "p06", text = "To be is to be perceived.", author = "George Berkeley", category = "philosophy", lang = "en"),
        Quote(id = "p07", text = "Happiness is the highest good.", author = "Aristotle", category = "philosophy", lang = "en"),
        Quote(id = "p08", text = "Man is condemned to be free.", author = "Jean-Paul Sartre", category = "philosophy", lang = "en"),
        Quote(id = "p09", text = "The life of man is solitary, poor, nasty, brutish, and short.", author = "Thomas Hobbes", category = "philosophy", lang = "en"),
        Quote(id = "p10", text = "Entities should not be multiplied without necessity.", author = "William of Ockham", category = "philosophy", lang = "en"),
        Quote(id = "p11", text = "Liberty consists in doing what one desires.", author = "John Stuart Mill", category = "philosophy", lang = "en"),
        Quote(id = "p12", text = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.", author = "Aristotle", category = "philosophy", lang = "en"),

        // --- PHILOSOPHY (Spanish) ---
        Quote(id = "p13", text = "Una vida sin examen no merece ser vivida.", author = "Socrates", category = "philosophy", lang = "es"),
        Quote(id = "p14", text = "Pienso, luego existo.", author = "Rene Descartes", category = "philosophy", lang = "es"),
        Quote(id = "p15", text = "Quien tiene un por que para vivir, puede soportar casi cualquier como.", author = "Friedrich Nietzsche", category = "philosophy", lang = "es"),
        Quote(id = "p16", text = "Somos lo que hacemos repetidamente. La excelencia no es un acto, sino un habito.", author = "Aristoteles", category = "philosophy", lang = "es"),
        Quote(id = "p17", text = "El hombre esta condenado a ser libre.", author = "Jean-Paul Sartre", category = "philosophy", lang = "es"),
        Quote(id = "p18", text = "La felicidad es el bien supremo.", author = "Aristoteles", category = "philosophy", lang = "es"),

        // --- DISCIPLINE (English) ---
        Quote(id = "d01", text = "Discipline is the bridge between goals and accomplishment.", author = "Jim Rohn", category = "discipline", lang = "en"),
        Quote(id = "d02", text = "We do not rise to the level of our goals; we fall to the level of our systems.", author = "James Clear", category = "discipline", lang = "en"),
        Quote(id = "d03", text = "The pain of discipline is nothing like the pain of disappointment.", author = "Justin Langer", category = "discipline", lang = "en"),
        Quote(id = "d04", text = "Small disciplines repeated with consistency every day lead to great achievements gained slowly over time.", author = "John C. Maxwell", category = "discipline", lang = "en"),
        Quote(id = "d05", text = "Motivation gets you going, but discipline keeps you growing.", author = "John C. Maxwell", category = "discipline", lang = "en"),
        Quote(id = "d06", text = "Freedom is nothing but a chance to be better.", author = "Albert Camus", category = "discipline", lang = "en"),
        Quote(id = "d07", text = "You will never always be motivated, so you must learn to be disciplined.", author = "Anonymous", category = "discipline", lang = "en"),
        Quote(id = "d08", text = "Success is nothing more than a few simple disciplines practiced every day.", author = "Jim Rohn", category = "discipline", lang = "en"),
        Quote(id = "d09", text = "Do what is hard and your life will be easy. Do what is easy and your life will be hard.", author = "Les Brown", category = "discipline", lang = "en"),
        Quote(id = "d10", text = "With self-discipline, most anything is possible.", author = "Theodore Roosevelt", category = "discipline", lang = "en"),

        // --- DISCIPLINE (Spanish) ---
        Quote(id = "d11", text = "La disciplina es el puente entre las metas y los logros.", author = "Jim Rohn", category = "discipline", lang = "es"),
        Quote(id = "d12", text = "No subimos al nivel de nuestras metas; caemos al nivel de nuestros sistemas.", author = "James Clear", category = "discipline", lang = "es"),
        Quote(id = "d13", text = "No siempre estaras motivado, asi que aprende a ser disciplinado.", author = "Anonimo", category = "discipline", lang = "es"),
        Quote(id = "d14", text = "El exito no es mas que unas pocas disciplinas simples practicadas cada dia.", author = "Jim Rohn", category = "discipline", lang = "es"),
        Quote(id = "d15", text = "Haz lo dificil y tu vida sera facil. Haz lo facil y tu vida sera dificil.", author = "Les Brown", category = "discipline", lang = "es"),

        // --- REFLECTION (English) ---
        Quote(id = "r01", text = "Knowing yourself is the beginning of all wisdom.", author = "Aristotle", category = "reflection", lang = "en"),
        Quote(id = "r02", text = "The only journey is the one within.", author = "Rainer Maria Rilke", category = "reflection", lang = "en"),
        Quote(id = "r03", text = "In the middle of difficulty lies opportunity.", author = "Albert Einstein", category = "reflection", lang = "en"),
        Quote(id = "r04", text = "What we think, we become.", author = "Buddha", category = "reflection", lang = "en"),
        Quote(id = "r05", text = "Turn your wounds into wisdom.", author = "Oprah Winfrey", category = "reflection", lang = "en"),
        Quote(id = "r06", text = "Life can only be understood backwards; but it must be lived forwards.", author = "Soren Kierkegaard", category = "reflection", lang = "en"),
        Quote(id = "r07", text = "The mind is everything. What you think you become.", author = "Buddha", category = "reflection", lang = "en"),
        Quote(id = "r08", text = "Not all those who wander are lost.", author = "J.R.R. Tolkien", category = "reflection", lang = "en"),
        Quote(id = "r09", text = "The wound is the place where the Light enters you.", author = "Rumi", category = "reflection", lang = "en"),
        Quote(id = "r10", text = "Until you make the unconscious conscious, it will direct your life and you will call it fate.", author = "Carl Jung", category = "reflection", lang = "en"),

        // --- REFLECTION (Spanish) ---
        Quote(id = "r11", text = "Conocerse a uno mismo es el principio de toda sabiduria.", author = "Aristoteles", category = "reflection", lang = "es"),
        Quote(id = "r12", text = "El unico viaje es el interior.", author = "Rainer Maria Rilke", category = "reflection", lang = "es"),
        Quote(id = "r13", text = "En medio de la dificultad yace la oportunidad.", author = "Albert Einstein", category = "reflection", lang = "es"),
        Quote(id = "r14", text = "Lo que pensamos, nos convertimos.", author = "Buda", category = "reflection", lang = "es"),
        Quote(id = "r15", text = "La vida solo puede entenderse hacia atras; pero debe vivirse hacia adelante.", author = "Soren Kierkegaard", category = "reflection", lang = "es"),
        Quote(id = "r16", text = "La herida es el lugar por donde entra la Luz.", author = "Rumi", category = "reflection", lang = "es"),
    )
    dao.insertAll(quotes)
}
