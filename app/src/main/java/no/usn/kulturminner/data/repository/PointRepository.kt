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
}

// ==================== Mapper ====================

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