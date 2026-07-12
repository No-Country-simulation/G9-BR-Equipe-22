from pydantic_settings import BaseSettings, SettingsConfigDict
from functools import lru_cache


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", protected_namespaces=())

    app_name: str = "TechTagger"
    app_version: str = "1.0.0"
    debug: bool = True

    models_dir: str = "./models"

    use_oci_storage: bool = False
    oci_namespace: str = ""
    oci_bucket_name: str = "techtagger-models"
    oci_config_file: str = "~/.oci/config"


@lru_cache()
def get_settings():
    return Settings()
