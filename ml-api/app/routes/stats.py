from fastapi import APIRouter
from app.schemas import StatsResponse, HealthResponse
from app.services.ml_service import MLService
from app.config import get_settings

router = APIRouter(tags=["System"])
settings = get_settings()
ml = MLService()


@router.get("/health", response_model=HealthResponse)
async def health_check():
    return HealthResponse(
        status="ok",
        app=settings.app_name,
        version=settings.app_version,
        model_loaded=True
    )


@router.get("/stats", response_model=StatsResponse)
async def estatisticas():
    stats = ml.get_stats()
    return StatsResponse(**stats)
