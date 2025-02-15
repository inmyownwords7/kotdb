Use Entities in the Repository Layer ✅

Entities represent the database structure and should only be used in repository interactions.

Use DTOs in the Service Layer ✅

Services should only expose DTOs so that changes to the database structure don't break external clients.