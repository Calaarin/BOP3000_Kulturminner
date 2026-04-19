package no.usn.kulturminner.data.source

import no.usn.kulturminner.data.api.PointApi
import no.usn.kulturminner.data.api.PointDto

class PointSource {

    suspend fun getAllPoints(): List<PointDto> =
        PointApi.service.getAllPoints()

    suspend fun getMyPoints(userId: String): List<PointDto> =
        PointApi.service.getMyPoints(userId)

    suspend fun getPoint(id: String): PointDto =
        PointApi.service.getPoint(id)

    suspend fun createPoint(pointDto: PointDto): PointDto =
        PointApi.service.createPoint(pointDto)

    suspend fun updatePoint(id: String, pointDto: PointDto): PointDto =
        PointApi.service.updatePoint(id, pointDto)

    suspend fun updatePointFull(id: String, pointDto: PointDto): PointDto =
        PointApi.service.updatePointFull(id, pointDto)

    /*
    suspend fun patchPoint(id: String, fields: PointPatchDto): PointDto =
    PointApi.service.patchPoint(id, fields)
     */

    suspend fun deletePoint(id: String) {
        PointApi.service.deletePoint(id)
    }
}