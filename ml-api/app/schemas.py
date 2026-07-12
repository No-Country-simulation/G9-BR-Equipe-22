from pydantic import BaseModel, Field, field_validator
from typing import List, Optional
from datetime import datetime


class ContentRequest(BaseModel):
    title: str = Field(..., min_length=3, max_length=500)
    text: str = Field(..., min_length=10, max_length=10000)

    @field_validator("title", "text")
    @classmethod
    def not_empty(cls, v):
        if not v.strip():
            raise ValueError("Campo não pode ser vazio")
        return v.strip()


class BatchContentRequest(BaseModel):
    items: List[ContentRequest] = Field(..., min_length=1, max_length=100)


class SearchRequest(BaseModel):
    query: str = Field(..., min_length=3, max_length=500)
    top_n: int = Field(5, ge=1, le=20)


class CategoryScore(BaseModel):
    name: str
    score: float


class RelatedContent(BaseModel):
    title: str
    category: str
    similarity: float
    url: Optional[str] = None


class ContentResponse(BaseModel):
    category: str
    probability: float
    top_categories: List[CategoryScore]
    keywords: List[str]
    related_content: List[RelatedContent]
    processed_at: datetime


class HealthResponse(BaseModel):
    model_config = {"protected_namespaces": ()}

    status: str
    app: str
    version: str
    model_loaded: bool


class StatsResponse(BaseModel):
    model_config = {"protected_namespaces": ()}

    total_content: int
    n_categories: int
    categories: List[str]
    model_version: str
    accuracy: float
    f1_score: float
    trained_at: str


class ErrorResponse(BaseModel):
    error: str
    detail: Optional[str] = None
