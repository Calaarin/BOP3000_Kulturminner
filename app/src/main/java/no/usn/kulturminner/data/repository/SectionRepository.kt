package no.usn.kulturminner.data.repository

import no.usn.kulturminner.data.api.SectionApi
import no.usn.kulturminner.data.api.SectionDto
import no.usn.kulturminner.data.model.Section

interface SectionRepository {
    suspend fun createSection(pointId: String, section: Section): Result<Unit>
    suspend fun deleteSection(id: String): Result<Unit>
    suspend fun updateSection(id: String, section: Section): Result<Unit>
}

class SectionRepositoryImpl : SectionRepository {

    override suspend fun createSection(pointId: String, section: Section): Result<Unit> = runCatching {
        SectionApi.service.createSection(
            SectionDto(
                pointId = pointId,
                heading = section.heading,
                text = section.text,
                imageUrl = section.imageUrl,
                videoUrl = section.videoUrl,
                sortOrder = section.sortOrder
            )
        )
    }

    override suspend fun deleteSection(id: String): Result<Unit> = runCatching {
        SectionApi.service.deleteSection(id)
    }
    // Det er sannsynlig at denne ikke kommer til å tas i bruk likevel av pragmatiske årsaker
    override suspend fun updateSection(id: String, section: Section): Result<Unit> = runCatching {
        SectionApi.service.updateSection(id, section.toDto())
    }
}

// ==================== Mapper ====================

private fun SectionDto.toModel() = Section(
    id = id,
    heading = heading,
    text = text,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    sortOrder = sortOrder
)

private fun Section.toDto() = SectionDto(
    id = id,
    heading = heading,
    text = text,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    sortOrder = sortOrder
)