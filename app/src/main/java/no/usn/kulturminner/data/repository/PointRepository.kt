package no.usn.kulturminner.data.repository

import no.usn.kulturminner.data.api.PointDto
import no.usn.kulturminner.data.api.SectionDto
import no.usn.kulturminner.data.model.Point
import no.usn.kulturminner.data.model.Section
import no.usn.kulturminner.data.source.PointSource
import java.time.Instant

interface PointRepository {
    suspend fun getAllPoints(): Result<List<Point>>
    suspend fun getMyPoints(userId: String): Result<List<Point>>
    suspend fun getPoint(id: String): Result<Point>
    suspend fun createPoint(point: Point): Result<Point>
    suspend fun updatePoint(point: Point): Result<Point>
    suspend fun deletePoint(id: String): Result<Unit>
    suspend fun getDummyPoints(): Result<List<Point>>
    suspend fun getSingleDummyPoint(id: String): Result<Point>
}

class PointRepositoryImpl(
    private val remoteSource: PointSource
) : PointRepository {

    override suspend fun getAllPoints(): Result<List<Point>> = runCatching {
        remoteSource.getAllPoints().map { it.toModel() }
    }

    override suspend fun getMyPoints(userId: String): Result<List<Point>> = runCatching {
        remoteSource.getMyPoints(userId).map { it.toModel() }
    }

    override suspend fun getPoint(id: String): Result<Point> = runCatching {
        remoteSource.getPoint(id).toModel()
    }

    override suspend fun createPoint(point: Point): Result<Point> = runCatching {
        val dto = point.toDto()
        remoteSource.createPoint(dto).toModel()
    }

    override suspend fun updatePoint(point: Point): Result<Point> = runCatching {
        val dto = point.toDto()
        remoteSource.updatePoint(point.id ?: "", dto).toModel()
    }

    override suspend fun deletePoint(id: String): Result<Unit> = runCatching {
        remoteSource.deletePoint(id)
    }

    override suspend fun getSingleDummyPoint(id: String): Result<Point> = runCatching {
        val points = getDummyPoints().getOrThrow()
        points.first { it.id == id }
    }

    // ==================== Dummy data for testing (generert av AI)  ====================

    override suspend fun getDummyPoints(): Result<List<Point>> = runCatching {
        listOf(
            Point(
                id = "p1",
                title = "Kulturstien",
                lat = 59.4123,
                lng = 9.0521,
                radius = 60,
                audioUrl = "https://example.com/audio1.mp3",
                sections = listOf(
                    Section(
                        heading = "Velkommen",
                        text = "Dette er starten på kulturstien som tar deg gjennom et område rikt på historie og natur. Her kan du oppleve både gamle kulturminner og vakre landskap langs en lett tilgjengelig sti. Underveis vil du finne informasjon om stedene du passerer, og du kan stoppe opp for å lære mer om områdets betydning gjennom tidene.",
                        imageUrl = "https://example.com/img1.jpg"
                    )
                ),
                createdAt = Instant.parse("2026-03-15T10:00:00Z"),
                updatedAt = Instant.parse("2026-04-02T14:30:00Z")
            ),
            Point(
                id = "p2",
                title = "Bø Prestegård",
                lat = 59.41694,
                lng = 9.05833,
                radius = 40,
                sections = listOf(
                    Section(
                        heading = null,
                        text = null,
                        imageUrl = "https://example.com/prestegard1.jpg",
                        videoUrl = "https://example.com/video1.mp4"
                    ),
                    Section(
                        heading = "Hagen",
                        text = "Hagen ved Bø Prestegård er kjent for sitt store mangfold av planter, inkludert flere sjeldne og historiske arter. Området har blitt brukt til både forskning og rekreasjon, og gir et unikt innblikk i hvordan hagebruk og botanikk har utviklet seg i regionen. Besøkende kan vandre fritt og oppleve de ulike sonene i hagen.",
                        imageUrl = null,
                        videoUrl = null
                    )
                ),
                createdAt = Instant.parse("2026-02-10T09:15:00Z"),
                updatedAt = Instant.parse("2026-04-01T11:20:00Z")
            ),
            Point(
                id = "p3",
                title = "Gullbring Kulturanlegg",
                lat = 59.41044,
                lng = 9.06212,
                radius = 80,
                sections = listOf(
                    Section(
                        heading = "Historikk",
                        text = "Gullbring Kulturanlegg ble bygget i 1985 og har siden vært et viktig samlingspunkt for kultur og idrett i området. Anlegget har blitt utvidet og modernisert flere ganger for å møte behovene til både lokale innbyggere og besøkende. Det er i dag en sentral arena for arrangementer og aktiviteter.",
                        imageUrl = "https://example.com/gullbring1.jpg",
                        videoUrl = null
                    ),
                    Section(
                        heading = "Konsertarena",
                        text = "Anlegget fungerer som en populær konsertarena hvor både lokale og nasjonale artister opptrer. Med gode fasiliteter og moderne lydanlegg tiltrekker stedet seg et bredt publikum gjennom hele året. Arrangementene spenner fra konserter og forestillinger til konferanser og kulturfestivaler.",
                        imageUrl = "https://example.com/gullbring2.jpg",
                        videoUrl = "https://example.com/gullbring_video.mp4"
                    ),
                    Section(
                        heading = "Fremtidsplaner",
                        text = "Det planlegges flere oppgraderinger av anlegget i årene som kommer, inkludert en ny scene og forbedrede publikumsområder. Målet er å gjøre Gullbring enda mer attraktivt som møteplass for kultur og opplevelser, samtidig som det tilpasses moderne krav og behov.",
                        imageUrl = null,
                        videoUrl = null
                    )
                ),
                createdAt = Instant.parse("2025-11-20T08:00:00Z"),
                updatedAt = Instant.parse("2026-03-28T16:45:00Z")
            ),
            Point(
                id = "p4",
                title = "Lifjell Utsiktspunkt",
                lat = 59.4437,
                lng = 9.1056,
                radius = 35,
                sections = listOf(
                    Section(
                        heading = "Panorama",
                        text = "Fra dette utsiktspunktet på Lifjell får du en fantastisk panoramautsikt over store deler av Telemark. På klare dager kan du se langt utover daler og fjellområder, noe som gjør dette til et populært mål for både fotografer og turgåere. Utsikten varierer med årstidene og gir alltid en unik opplevelse.",
                        imageUrl = "https://example.com/lifjell1.jpg",
                        videoUrl = null
                    ),
                    Section(
                        heading = null,
                        text = null,
                        imageUrl = "https://example.com/lifjell2.jpg",
                        videoUrl = null
                    ),
                    Section(
                        heading = "Turinfo",
                        text = "Turen opp til utsiktspunktet tar omtrent 45 minutter fra nærmeste parkeringsplass og går gjennom variert terreng. Stien er godt merket og egner seg for de fleste, men kan være noe bratt enkelte steder. Det anbefales gode sko og å ta med vann, spesielt på varme dager.",
                        imageUrl = null,
                        videoUrl = null
                    ),
                    Section(
                        heading = "Vær obs",
                        text = "Vær oppmerksom på at enkelte partier kan være glatte, spesielt etter regn eller i vinterhalvåret. Det er viktig å ferdes forsiktig og tilpasse tempoet etter forholdene. Husk også å vise hensyn til naturen og andre turgåere underveis.",
                        imageUrl = null,
                        videoUrl = null
                    )
                ),
                createdAt = Instant.parse("2026-01-05T12:00:00Z"),
                updatedAt = Instant.parse("2026-04-05T09:10:00Z")
            )
        )
    }
}

// ==================== Mapper ====================

// (Disse må oppdateres etter behov for hva som skal sendes før de er brukbare)

private fun PointDto.toModel(): Point {
    return Point(
        id = id,
        title = title,
        lat = lat,
        lng = lng,
        radius = radius,
        audioUrl = audioUrl,
        sections = sections.map { it.toModel() },

        // Konverterer String til Instant (null-safe)
        createdAt = createdAt?.let { Instant.parse(it) },
        updatedAt = updatedAt?.let { Instant.parse(it) }
    )
}

private fun Point.toDto(): PointDto {
    return PointDto(
        id = id,
        title = title,
        lat = lat,
        lng = lng,
        radius = radius,
        audioUrl = audioUrl,
        sections = sections.map { it.toDto() },

        // Konverterer Instant til String (såkalt ISO-8601 format)
        createdAt = createdAt?.toString(),
        updatedAt = updatedAt?.toString()
    )
}

private fun SectionDto.toModel() = Section(
    id = id,
    heading = heading,
    text = text,
    imageUrl = imageUrl,
    videoUrl = videoUrl
)

private fun Section.toDto() = SectionDto(
    id = id,
    heading = heading,
    text = text,
    imageUrl = imageUrl,
    videoUrl = videoUrl
)



//
