package com.christianhernandez.quoteflow.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.christianhernandez.quoteflow.data.model.Quote
import com.christianhernandez.quoteflow.util.AuthorPortraits

/**
 * Bottom sheet showing author details: large photo, name, biography, and sample quotes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorDetailSheet(
    authorName: String,
    authorQuotes: List<Quote>,
    language: String,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val portraitUrl = AuthorPortraits.getPortraitUrl(authorName)
    val bio = getAuthorBio(authorName, language)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Author photo (large — always available via Wikimedia or UI Avatars)
            item {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(portraitUrl)
                        .crossfade(true)
                        .addHeader("User-Agent", "QuoteFlow/1.0 (Android)")
                        .build(),
                    contentDescription = authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Author name
                Text(
                    text = authorName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Biography
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // "Quotes by this author" header
                if (authorQuotes.isNotEmpty()) {
                    Text(
                        text = if (language == "es") "Frases de este autor" else "Quotes by this author",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Sample quotes
            items(authorQuotes.take(5)) { quote ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ),
                ) {
                    Text(
                        text = "\"${quote.text}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
    }
}

/**
 * Returns a short biography for known authors. Falls back to a generic description.
 */
private fun getAuthorBio(name: String, lang: String): String {
    val key = name.lowercase().trim()
    val isES = lang == "es"

    val bios = mapOf(
        "marcus aurelius" to Pair(
            "Emperador romano (121-180 d.C.) y filosofo estoico. Su obra 'Meditaciones' es uno de los textos mas influyentes del estoicismo, escrito como reflexiones personales durante sus campanas militares.",
            "Roman Emperor (121-180 AD) and Stoic philosopher. His work 'Meditations' is one of the most influential Stoic texts, written as personal reflections during his military campaigns."
        ),
        "marco aurelio" to Pair(
            "Emperador romano (121-180 d.C.) y filosofo estoico. Su obra 'Meditaciones' es uno de los textos mas influyentes del estoicismo.",
            "Roman Emperor (121-180 AD) and Stoic philosopher. His work 'Meditations' is one of the most influential Stoic texts."
        ),
        "seneca" to Pair(
            "Filosofo estoico romano, dramaturgo y consejero politico (4 a.C. - 65 d.C.). Sus cartas y ensayos sobre la brevedad de la vida y la tranquilidad del alma siguen siendo profundamente relevantes.",
            "Roman Stoic philosopher, dramatist, and political advisor (4 BC - 65 AD). His letters and essays on the shortness of life and tranquility of mind remain profoundly relevant."
        ),
        "epictetus" to Pair(
            "Filosofo estoico griego (50-135 d.C.) nacido en esclavitud. Enseno que la filosofia es una forma de vida, no solo una disciplina teorica. Su 'Enquiridion' es una guia practica para vivir bien.",
            "Greek Stoic philosopher (50-135 AD) born into slavery. He taught that philosophy is a way of life, not merely a theoretical discipline. His 'Enchiridion' is a practical guide for living well."
        ),
        "plato" to Pair(
            "Filosofo griego (428-348 a.C.), fundador de la Academia de Atenas. Sus dialogos exploraron la justicia, la belleza, la igualdad y la naturaleza del conocimiento.",
            "Greek philosopher (428-348 BC), founder of the Academy in Athens. His dialogues explored justice, beauty, equality, and the nature of knowledge."
        ),
        "aristotle" to Pair(
            "Filosofo griego (384-322 a.C.), estudiante de Platon y tutor de Alejandro Magno. Sus obras abarcan logica, metafisica, etica, politica y ciencias naturales.",
            "Greek philosopher (384-322 BC), student of Plato and tutor of Alexander the Great. His works span logic, metaphysics, ethics, politics, and natural sciences."
        ),
        "socrates" to Pair(
            "Filosofo griego (470-399 a.C.), considerado el padre de la filosofia occidental. Conocido por su metodo dialectico y la maxima 'Solo se que no se nada'.",
            "Greek philosopher (470-399 BC), considered the father of Western philosophy. Known for his dialectical method and the maxim 'I know that I know nothing'."
        ),
        "nietzsche" to Pair(
            "Filosofo aleman (1844-1900). Sus ideas sobre el 'superhombre', la voluntad de poder y la muerte de Dios transformaron el pensamiento moderno.",
            "German philosopher (1844-1900). His ideas about the 'superman', will to power, and the death of God transformed modern thought."
        ),
        "friedrich nietzsche" to Pair(
            "Filosofo aleman (1844-1900). Sus ideas sobre el 'superhombre', la voluntad de poder y la muerte de Dios transformaron el pensamiento moderno.",
            "German philosopher (1844-1900). His ideas about the 'superman', will to power, and the death of God transformed modern thought."
        ),
        "albert camus" to Pair(
            "Escritor y filosofo franco-argelino (1913-1960), premio Nobel de Literatura. Exponente del absurdismo, exploro el significado de la vida ante un universo indiferente.",
            "Franco-Algerian writer and philosopher (1913-1960), Nobel Prize in Literature. Proponent of absurdism, he explored the meaning of life in an indifferent universe."
        ),
        "confucius" to Pair(
            "Filosofo chino (551-479 a.C.) cuyas ensenanzas sobre etica, familia y gobierno han influido en la civilizacion oriental durante mas de 2.500 anos.",
            "Chinese philosopher (551-479 BC) whose teachings on ethics, family, and governance have influenced Eastern civilization for over 2,500 years."
        ),
        "lao tzu" to Pair(
            "Filosofo chino, autor legendario del Tao Te Ching y fundador del taoismo. Sus ensenanzas enfatizan vivir en armonia con el Tao (el Camino).",
            "Chinese philosopher, legendary author of the Tao Te Ching and founder of Taoism. His teachings emphasize living in harmony with the Tao (the Way)."
        ),
        "buddha" to Pair(
            "Siddhartha Gautama (c. 563-483 a.C.), fundador del budismo. Alcanzo la iluminacion y enseno el camino medio para la liberacion del sufrimiento.",
            "Siddhartha Gautama (c. 563-483 BC), founder of Buddhism. He attained enlightenment and taught the middle way to liberation from suffering."
        ),
    )

    val bio = bios[key]
    if (bio != null) {
        return if (isES) bio.first else bio.second
    }

    // Generic fallback
    return if (isES) {
        "Pensador y autor cuyas palabras han inspirado a generaciones. Explora sus frases para descubrir su filosofia."
    } else {
        "Thinker and author whose words have inspired generations. Explore their quotes to discover their philosophy."
    }
}
