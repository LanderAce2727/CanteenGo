from typing import Annotated, Literal
from uuid import UUID
from pydantic import BaseModel, ConfigDict, Field, StringConstraints

Username = Annotated[str, StringConstraints(strip_whitespace=True, pattern=r"^[a-z0-9][a-z0-9_.-]{2,39}$")]
FoodId = Annotated[str, StringConstraints(pattern=r"^[a-z0-9_-]{1,40}$")]

class StrictModel(BaseModel):
    model_config = ConfigDict(extra="forbid", str_strip_whitespace=True)

class Credentials(StrictModel):
    username: Username
    # Password whitespace is meaningful.
    password: Annotated[str, Field(min_length=12, max_length=128)]
    model_config = ConfigDict(extra="forbid", str_strip_whitespace=False)

class OrderLine(StrictModel):
    food_id: FoodId
    quantity: Annotated[int, Field(strict=True, ge=1, le=99)]

class NewOrder(StrictModel):
    customer: Annotated[str, StringConstraints(min_length=1, max_length=80, pattern=r"^[^\x00-\x1f\x7f]+$")]
    items: list[OrderLine] = Field(min_length=1, max_length=30)
    request_id: UUID

class StatusChange(StrictModel):
    status: Literal["PREPARING", "READY", "COMPLETED"]

class StockChange(StrictModel):
    stock: Annotated[int, Field(strict=True, ge=0, le=10000)]
