package com.christianhernandez.quoteflow.util

/**
 * Wikimedia Commons portrait URLs for known philosophers and authors.
 * Ported from MindScrolling Flutter app's author_avatar.dart.
 * All images are public domain portraits from Wikimedia Commons.
 *
 * For authors not in the Wikimedia list, generates a UI Avatars fallback
 * with the author's initials on a colored background.
 */
object AuthorPortraits {

    /**
     * Returns a portrait URL for the given author name.
     * Uses Wikimedia Commons if available, otherwise generates a UI Avatars fallback.
     * Never returns null — every author gets a portrait.
     */
    fun getPortraitUrl(name: String): String {
        val key = name.lowercase().trim()
        // Try Wikimedia first
        knownPortraits[key]?.let { return it }

        // Fallback: generate avatar with UI Avatars API
        val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
        val colors = listOf("3B82F6", "10B981", "F97316", "8B5CF6", "6366F1", "EF4444")
        val bgColor = colors[name.length % colors.size]
        return "https://ui-avatars.com/api/?name=$encodedName&size=200&background=$bgColor&color=fff&bold=true&font-size=0.4&rounded=true"
    }

    // Wikimedia Commons thumbnails (public domain portraits)
    // Format: 200px width for efficient mobile loading
    private val knownPortraits = mapOf(
        // Greek / Roman
        "marcus aurelius" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/MSR-ra-61-b-1-DM.jpg/200px-MSR-ra-61-b-1-DM.jpg",
        "marco aurelio" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/MSR-ra-61-b-1-DM.jpg/200px-MSR-ra-61-b-1-DM.jpg",
        "seneca" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Seneca-berlinantikensammlung-1.jpg/200px-Seneca-berlinantikensammlung-1.jpg",
        "séneca" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Seneca-berlinantikensammlung-1.jpg/200px-Seneca-berlinantikensammlung-1.jpg",
        "seneca the younger" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6e/Seneca-berlinantikensammlung-1.jpg/200px-Seneca-berlinantikensammlung-1.jpg",
        "epictetus" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Epicteti_Enchiridion_Latinis_versibus_adumbratum_%28Oxford_1715%29_frontispiece.jpg/200px-Epicteti_Enchiridion_Latinis_versibus_adumbratum_%28Oxford_1715%29_frontispiece.jpg",
        "epicteto" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Epicteti_Enchiridion_Latinis_versibus_adumbratum_%28Oxford_1715%29_frontispiece.jpg/200px-Epicteti_Enchiridion_Latinis_versibus_adumbratum_%28Oxford_1715%29_frontispiece.jpg",
        "plato" to "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Plato_Silanion_Musei_Capitolini_MC1377.jpg/200px-Plato_Silanion_Musei_Capitolini_MC1377.jpg",
        "platón" to "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Plato_Silanion_Musei_Capitolini_MC1377.jpg/200px-Plato_Silanion_Musei_Capitolini_MC1377.jpg",
        "aristotle" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Aristotle_Altemps_Inv8575.jpg/200px-Aristotle_Altemps_Inv8575.jpg",
        "aristóteles" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Aristotle_Altemps_Inv8575.jpg/200px-Aristotle_Altemps_Inv8575.jpg",
        "socrates" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Socrate_du_Louvre.jpg/200px-Socrate_du_Louvre.jpg",
        "sócrates" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Socrate_du_Louvre.jpg/200px-Socrate_du_Louvre.jpg",
        "heraclitus" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/67/Heraclitus%2C_Johannes_Moreelse.jpg/200px-Heraclitus%2C_Johannes_Moreelse.jpg",
        "heráclito" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/67/Heraclitus%2C_Johannes_Moreelse.jpg/200px-Heraclitus%2C_Johannes_Moreelse.jpg",
        "pythagoras" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Kapitolinischer_Pythagoras_adjusted.jpg/200px-Kapitolinischer_Pythagoras_adjusted.jpg",
        "pitágoras" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Kapitolinischer_Pythagoras_adjusted.jpg/200px-Kapitolinischer_Pythagoras_adjusted.jpg",
        "democritus" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Democritus2.jpg/200px-Democritus2.jpg",
        "demócrito" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Democritus2.jpg/200px-Democritus2.jpg",
        "cicero" to "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/M-T-Cicero.jpg/200px-M-T-Cicero.jpg",
        "cicerón" to "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/M-T-Cicero.jpg/200px-M-T-Cicero.jpg",
        "marcus tullius cicero" to "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/M-T-Cicero.jpg/200px-M-T-Cicero.jpg",
        "epicurus" to "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Epikouros_BM_1843.jpg/200px-Epikouros_BM_1843.jpg",
        "epicuro" to "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Epikouros_BM_1843.jpg/200px-Epikouros_BM_1843.jpg",
        "plutarch" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Plutarch_of_Chaeronea-03-removebg-preview.png/200px-Plutarch_of_Chaeronea-03-removebg-preview.png",
        "plutarco" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Plutarch_of_Chaeronea-03-removebg-preview.png/200px-Plutarch_of_Chaeronea-03-removebg-preview.png",
        "sophocles" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Sophocles_pushkin.jpg/200px-Sophocles_pushkin.jpg",
        "sófocles" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/19/Sophocles_pushkin.jpg/200px-Sophocles_pushkin.jpg",
        "virgil" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Virgil_mosaic_crop.jpg/200px-Virgil_mosaic_crop.jpg",
        "virgilio" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/Virgil_mosaic_crop.jpg/200px-Virgil_mosaic_crop.jpg",
        "aeschylus" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c0/Aischylos_B%C3%BCste.jpg/200px-Aischylos_B%C3%BCste.jpg",
        "esquilo" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c0/Aischylos_B%C3%BCste.jpg/200px-Aischylos_B%C3%BCste.jpg",
        "euripides" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Euripides_Pio-Clementino_Inv302.jpg/200px-Euripides_Pio-Clementino_Inv302.jpg",
        "eurípides" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Euripides_Pio-Clementino_Inv302.jpg/200px-Euripides_Pio-Clementino_Inv302.jpg",
        "ovid" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Ovid_Metamorphosen_Kupfertitel.jpg/200px-Ovid_Metamorphosen_Kupfertitel.jpg",
        "ovidio" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Ovid_Metamorphosen_Kupfertitel.jpg/200px-Ovid_Metamorphosen_Kupfertitel.jpg",
        "thomas aquinas" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/St-thomas-aquinas.jpg/200px-St-thomas-aquinas.jpg",
        "santo tomás de aquino" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/St-thomas-aquinas.jpg/200px-St-thomas-aquinas.jpg",
        "alexander the great" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/Alexander_the_Great_mosaic.jpg/200px-Alexander_the_Great_mosaic.jpg",
        "alejandro magno" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/Alexander_the_Great_mosaic.jpg/200px-Alexander_the_Great_mosaic.jpg",

        // Modern philosophers
        "nietzsche" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Nietzsche187a.jpg/200px-Nietzsche187a.jpg",
        "friedrich nietzsche" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Nietzsche187a.jpg/200px-Nietzsche187a.jpg",
        "immanuel kant" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f2/Kant_gemaelde_3.jpg/200px-Kant_gemaelde_3.jpg",
        "arthur schopenhauer" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Arthur_Schopenhauer_by_J_Sch%C3%A4fer%2C_1859b.jpg/200px-Arthur_Schopenhauer_by_J_Sch%C3%A4fer%2C_1859b.jpg",
        "voltaire" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/D%27apr%C3%A8s_Maurice_Quentin_de_La_Tour%2C_Portrait_de_Voltaire%2C_d%C3%A9tail_du_visage_%28ch%C3%A2teau_de_Ferney%29.jpg/200px-D%27apr%C3%A8s_Maurice_Quentin_de_La_Tour%2C_Portrait_de_Voltaire%2C_d%C3%A9tail_du_visage_%28ch%C3%A2teau_de_Ferney%29.jpg",
        "albert camus" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/08/Albert_Camus%2C_gagnant_de_prix_Nobel%2C_portrait_en_buste%2C_pos%C3%A9_au_bureau%2C_faisant_face_%C3%A0_gauche%2C_cigarette_de_tabagisme.jpg/200px-Albert_Camus%2C_gagnant_de_prix_Nobel%2C_portrait_en_buste%2C_pos%C3%A9_au_bureau%2C_faisant_face_%C3%A0_gauche%2C_cigarette_de_tabagisme.jpg",
        "jean-paul sartre" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ef/Sartre_1967_crop.jpg/200px-Sartre_1967_crop.jpg",
        "simone de beauvoir" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Simone_de_Beauvoir2.png/200px-Simone_de_Beauvoir2.png",
        "soren kierkegaard" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Kierkegaard.jpg/200px-Kierkegaard.jpg",
        "rené descartes" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Frans_Hals_-_Portret_van_Ren%C3%A9_Descartes.jpg/200px-Frans_Hals_-_Portret_van_Ren%C3%A9_Descartes.jpg",
        "ludwig wittgenstein" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Ludwig_Wittgenstein.jpg/200px-Ludwig_Wittgenstein.jpg",
        "baruch spinoza" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Spinoza.jpg/200px-Spinoza.jpg",
        "blaise pascal" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/Blaise_Pascal_Versailles.JPG/200px-Blaise_Pascal_Versailles.JPG",
        "bertrand russell" to "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Bertrand_Russell_1957.jpg/200px-Bertrand_Russell_1957.jpg",

        // Eastern philosophy
        "confucius" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Confucius_Tang_Dynasty.jpg/200px-Confucius_Tang_Dynasty.jpg",
        "confucio" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Confucius_Tang_Dynasty.jpg/200px-Confucius_Tang_Dynasty.jpg",
        "lao tzu" to "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Zhang_Lu-Laozi_Riding_an_Ox.jpg/200px-Zhang_Lu-Laozi_Riding_an_Ox.jpg",
        "lao tse" to "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Zhang_Lu-Laozi_Riding_an_Ox.jpg/200px-Zhang_Lu-Laozi_Riding_an_Ox.jpg",
        "buddha" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Gandhara_Buddha_%28tnm%29.jpeg/200px-Gandhara_Buddha_%28tnm%29.jpeg",
        "buda" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Gandhara_Buddha_%28tnm%29.jpeg/200px-Gandhara_Buddha_%28tnm%29.jpeg",
        "the buddha" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Gandhara_Buddha_%28tnm%29.jpeg/200px-Gandhara_Buddha_%28tnm%29.jpeg",
        "rumi" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Mevlana_Celaleddin_Rumi.jpg/200px-Mevlana_Celaleddin_Rumi.jpg",
        "khalil gibran" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/34/Kahlil_Gibran_1913.jpg/200px-Kahlil_Gibran_1913.jpg",
        "kahlil gibran" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/34/Kahlil_Gibran_1913.jpg/200px-Kahlil_Gibran_1913.jpg",
        "dalai lama" to "https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/Dalailama1_20121014_4639.jpg/200px-Dalailama1_20121014_4639.jpg",
        "thich nhat hanh" to "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/Thich_Nhat_Hanh_12_%28cropped%29.jpg/200px-Thich_Nhat_Hanh_12_%28cropped%29.jpg",
        "thích nhất hạnh" to "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/Thich_Nhat_Hanh_12_%28cropped%29.jpg/200px-Thich_Nhat_Hanh_12_%28cropped%29.jpg",
        "alan watts" to "https://upload.wikimedia.org/wikipedia/en/thumb/f/f7/Alan_Watts_portrait.jpg/200px-Alan_Watts_portrait.jpg",
        "rabindranath tagore" to "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Rabindranath_Tagore_in_1909.jpg/200px-Rabindranath_Tagore_in_1909.jpg",

        // Writers & thinkers
        "fyodor dostoevsky" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Vasily_Perov_-_%D0%9F%D0%BE%D1%80%D1%82%D1%80%D0%B5%D1%82_%D0%A4.%D0%9C.%D0%94%D0%BE%D1%81%D1%82%D0%BE%D0%B5%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_-_Google_Art_Project.jpg/200px-Vasily_Perov_-_%D0%9F%D0%BE%D1%80%D1%82%D1%80%D0%B5%D1%82_%D0%A4.%D0%9C.%D0%94%D0%BE%D1%81%D1%82%D0%BE%D0%B5%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_-_Google_Art_Project.jpg",
        "fiódor dostoyevski" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Vasily_Perov_-_%D0%9F%D0%BE%D1%80%D1%82%D1%80%D0%B5%D1%82_%D0%A4.%D0%9C.%D0%94%D0%BE%D1%81%D1%82%D0%BE%D0%B5%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_-_Google_Art_Project.jpg/200px-Vasily_Perov_-_%D0%9F%D0%BE%D1%80%D1%82%D1%80%D0%B5%D1%82_%D0%A4.%D0%9C.%D0%94%D0%BE%D1%81%D1%82%D0%BE%D0%B5%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_-_Google_Art_Project.jpg",
        "leo tolstoy" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c6/L.N.Tolstoy_Prokudin-Gorsky.jpg/200px-L.N.Tolstoy_Prokudin-Gorsky.jpg",
        "león tolstói" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c6/L.N.Tolstoy_Prokudin-Gorsky.jpg/200px-L.N.Tolstoy_Prokudin-Gorsky.jpg",
        "ralph waldo emerson" to "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Ralph_Waldo_Emerson_ca1857_retouched.jpg/200px-Ralph_Waldo_Emerson_ca1857_retouched.jpg",
        "oscar wilde" to "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9c/Oscar_Wilde_portrait.jpg/200px-Oscar_Wilde_portrait.jpg",
        "victor hugo" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Victor_Hugo_by_%C3%89tienne_Carjat_1876_-_full.jpg/200px-Victor_Hugo_by_%C3%89tienne_Carjat_1876_-_full.jpg",
        "víctor hugo" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Victor_Hugo_by_%C3%89tienne_Carjat_1876_-_full.jpg/200px-Victor_Hugo_by_%C3%89tienne_Carjat_1876_-_full.jpg",
        "mark twain" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Mark_Twain_by_AF_Bradley.jpg/200px-Mark_Twain_by_AF_Bradley.jpg",
        "william shakespeare" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Shakespeare.jpg/200px-Shakespeare.jpg",
        "ernest hemingway" to "https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/ErnestHemingway.jpg/200px-ErnestHemingway.jpg",
        "johann wolfgang von goethe" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Goethe_%28Stieler_1828%29.jpg/200px-Goethe_%28Stieler_1828%29.jpg",
        "charles dickens" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/Dickens_Gurney_head.jpg/200px-Dickens_Gurney_head.jpg",
        "jorge luis borges" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Jorge_Luis_Borges_1951%2C_by_Grete_Stern.jpg/200px-Jorge_Luis_Borges_1951%2C_by_Grete_Stern.jpg",
        "miguel de cervantes" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Cervantes_J%C3%A1uregui.jpg/200px-Cervantes_J%C3%A1uregui.jpg",
        "george orwell" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/George_Orwell_press_photo.jpg/200px-George_Orwell_press_photo.jpg",
        "aldous huxley" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Aldous_Huxley_psychedelic_experience.jpg/200px-Aldous_Huxley_psychedelic_experience.jpg",
        "george bernard shaw" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/George_Bernard_Shaw_1936.jpg/200px-George_Bernard_Shaw_1936.jpg",
        "antoine de saint-exupéry" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e8/Saint-Exup%C3%A9ry_par_un_photographe_inconnu.jpg/200px-Saint-Exup%C3%A9ry_par_un_photographe_inconnu.jpg",
        "kurt vonnegut" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Kurt_Vonnegut_1972.jpg/200px-Kurt_Vonnegut_1972.jpg",
        "haruki murakami" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Murakami_Haruki_%282009%29.jpg/200px-Murakami_Haruki_%282009%29.jpg",
        "anaïs nin" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Anais_Nin.jpg/200px-Anais_Nin.jpg",
        "paulo coelho" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/25_de_janeiro_de_2019_-_Paulo_Coelho_%28cropped_2%29.jpg/200px-25_de_janeiro_de_2019_-_Paulo_Coelho_%28cropped_2%29.jpg",
        "henry david thoreau" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/ba/Henry_David_Thoreau.jpg/200px-Henry_David_Thoreau.jpg",

        // Leaders & public figures
        "mahatma gandhi" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Mahatma-Gandhi%2C_studio%2C_1931.jpg/200px-Mahatma-Gandhi%2C_studio%2C_1931.jpg",
        "nelson mandela" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Nelson_Mandela_1994.jpg/200px-Nelson_Mandela_1994.jpg",
        "martin luther king jr." to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Martin_Luther_King%2C_Jr..jpg/200px-Martin_Luther_King%2C_Jr..jpg",
        "winston churchill" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Sir_Winston_Churchill_-_19086236948.jpg/200px-Sir_Winston_Churchill_-_19086236948.jpg",
        "albert einstein" to "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Albert_Einstein_Head.jpg/200px-Albert_Einstein_Head.jpg",
        "benjamin franklin" to "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Joseph_Siffrein_Duplessis_-_Benjamin_Franklin_-_Google_Art_Project.jpg/200px-Joseph_Siffrein_Duplessis_-_Benjamin_Franklin_-_Google_Art_Project.jpg",
        "abraham lincoln" to "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Abraham_Lincoln_O-77_matte_collodion_print.jpg/200px-Abraham_Lincoln_O-77_matte_collodion_print.jpg",
        "theodore roosevelt" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/President_Roosevelt_-_Pach_Bros.jpg/200px-President_Roosevelt_-_Pach_Bros.jpg",
        "thomas jefferson" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/Official_Presidential_portrait_of_Thomas_Jefferson_%28by_Rembrandt_Peale%2C_1800%29%28cropped%29.jpg/200px-Official_Presidential_portrait_of_Thomas_Jefferson_%28by_Rembrandt_Peale%2C_1800%29%28cropped%29.jpg",
        "john f. kennedy" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/John_F._Kennedy%2C_White_House_color_photo_portrait.jpg/200px-John_F._Kennedy%2C_White_House_color_photo_portrait.jpg",
        "franklin d. roosevelt" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/FDR_1944_Color_Portrait.jpg/200px-FDR_1944_Color_Portrait.jpg",
        "eleanor roosevelt" to "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Eleanor_Roosevelt_portrait_1933.jpg/200px-Eleanor_Roosevelt_portrait_1933.jpg",
        "napoleon" to "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Jacques-Louis_David_-_The_Emperor_Napoleon_in_His_Study_at_the_Tuileries_-_Google_Art_Project.jpg/200px-Jacques-Louis_David_-_The_Emperor_Napoleon_in_His_Study_at_the_Tuileries_-_Google_Art_Project.jpg",

        // Scientists & innovators
        "galileo galilei" to "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Justus_Sustermans_-_Portrait_of_Galileo_Galilei%2C_1636.jpg/200px-Justus_Sustermans_-_Portrait_of_Galileo_Galilei%2C_1636.jpg",
        "marie curie" to "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Marie_Curie_c._1920s.jpg/200px-Marie_Curie_c._1920s.jpg",
        "thomas edison" to "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9d/Thomas_Edison2.jpg/200px-Thomas_Edison2.jpg",
        "steve jobs" to "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dc/Steve_Jobs_Headshot_2010-CROP_%28cropped_2%29.jpg/200px-Steve_Jobs_Headshot_2010-CROP_%28cropped_2%29.jpg",
        "henry ford" to "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Henry_ford_1919.jpg/200px-Henry_ford_1919.jpg",
        "albert schweitzer" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Bundesarchiv_Bild_183-D0116-0041-019%2C_Albert_Schweitzer.jpg/200px-Bundesarchiv_Bild_183-D0116-0041-019%2C_Albert_Schweitzer.jpg",

        // Other notable figures
        "carl jung" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/CGJung.jpg/200px-CGJung.jpg",
        "viktor frankl" to "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Viktor_Frankl2.jpg/200px-Viktor_Frankl2.jpg",
        "bruce lee" to "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/Bruce_Lee_1973.jpg/200px-Bruce_Lee_1973.jpg",
        "napoleon hill" to "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Napoleon_Hill_headshot.jpg/200px-Napoleon_Hill_headshot.jpg",
        "maya angelou" to "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4f/Angelou_at_Clinton_inauguration_%28cropped_2%29.jpg/200px-Angelou_at_Clinton_inauguration_%28cropped_2%29.jpg",
        "mother teresa" to "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d6/Mother_Teresa_1.jpg/200px-Mother_Teresa_1.jpg",
        "helen keller" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/Helen_KellerA.jpg/200px-Helen_KellerA.jpg",
        "john lennon" to "https://upload.wikimedia.org/wikipedia/commons/thumb/8/85/John_Lennon_1969_%28cropped%29.jpg/200px-John_Lennon_1969_%28cropped%29.jpg",
        "pablo picasso" to "https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Pablo_picasso_1.jpg/200px-Pablo_picasso_1.jpg",
        "wayne dyer" to "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/DrWayneDyer2009.jpg/200px-DrWayneDyer2009.jpg",
        "oprah winfrey" to "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bf/Oprah_in_2014.jpg/200px-Oprah_in_2014.jpg",
        "coco chanel" to "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Coco_Chanel%2C_1920.jpg/200px-Coco_Chanel%2C_1920.jpg",
        "francis of assisi" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Cimabue_-_Saint_Francis.jpg/200px-Cimabue_-_Saint_Francis.jpg",
        "san francisco de asís" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/Cimabue_-_Saint_Francis.jpg/200px-Cimabue_-_Saint_Francis.jpg",
    )
}
