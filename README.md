# Internship Tracker

A Spring Boot REST API for keeping track of internship applications. You register with an
email and password, log in to get a JWT, and then create, list, filter, update and delete your
applications. Every query is scoped to the signed-in user, so one account can never read or
change another account's applications. By default it stores data in an in-memory H2 database,
so it runs from a fresh clone with nothing to install beyond a JDK.

**Stack:** Java 21 · Spring Boot 4.0 · Spring Security · Spring Data JPA · H2 · jjwt 0.11 · Lombok

---

## Features

- **Accounts:** register and log in with email and password. Passwords are hashed with BCrypt;
  emails are trimmed and lower-cased, so `Ada@Example.com` and `ada@example.com` are the same
  account.
- **Stateless JWT auth:** login returns an HMAC-signed token (subject = email, expiry
  configurable). No server-side sessions.
- **Per-user data:** every application belongs to the user who created it. Asking for someone
  else's application returns `404`, the same as one that does not exist.
- **CRUD** for applications: company, role, location, status, applied date and notes.
- **Filtering, paging and sorting** on the list endpoint: filter by status and/or a
  case-insensitive company substring; page with `page` and `size`; sort with
  `sort=field,asc|desc`.
- **Validation** with per-field error messages.
- **H2 console** at `/h2-console` for inspecting the development database.

---

## API

All bodies are JSON. Endpoints marked "Bearer" need `Authorization: Bearer <token>`.

| Method   | Path                     | Auth   | Body                     | Success                       |
| -------- | ------------------------ | ------ | ------------------------ | ----------------------------- |
| `POST`   | `/api/auth/register`     | None   | `{ email, password }`    | `201`, empty body             |
| `POST`   | `/api/auth/login`        | None   | `{ email, password }`    | `200` `{ "token": "..." }`    |
| `POST`   | `/api/applications`      | Bearer | application (see below)  | `201` the created application |
| `GET`    | `/api/applications`      | Bearer | none                     | `200` a page of applications  |
| `GET`    | `/api/applications/{id}` | Bearer | none                     | `200` one application         |
| `PUT`    | `/api/applications/{id}` | Bearer | application (see below)  | `200` the updated application |
| `DELETE` | `/api/applications/{id}` | Bearer | none                     | `204`                         |

**Register / login body.** `email` must be a well-formed address; on register, `password`
must be 8 to 72 characters (72 is BCrypt's input limit).

**Application body** (create and update):

```jsonc
{
  "company": "Acme",                          // required
  "roleTitle": "Software Engineering Intern", // required
  "status": "APPLIED",                        // required, see ApplicationStatus
  "location": "Remote",                       // optional
  "appliedDate": "2026-10-01",                // optional, YYYY-MM-DD
  "notes": "Referred by a friend"             // optional, up to 2000 characters
}
```

`PUT` replaces the whole application: optional fields you leave out are cleared.

**List query parameters:** `status` (one `ApplicationStatus` value), `company` (substring,
case-insensitive), `page` (0-based, default 0), `size` (default 20), `sort` (e.g.
`sort=appliedDate,desc`). The response is a Spring Data page: the items are in `content`,
with `totalElements`, `totalPages`, `number` and `size` alongside.

**Response shape.** An application comes back as
`{ id, company, roleTitle, location, status, appliedDate, note }`. Note that the response
field is `note`, while the request field is `notes`.

**Errors.**

| Status | When                                                                                   |
| ------ | -------------------------------------------------------------------------------------- |
| `400`  | Validation failed. The body has a `fields` object mapping each field to its message.   |
| `404`  | The application does not exist or belongs to another user.                             |
| `403`  | Missing, invalid or expired token. Also returned, with an empty body, for a wrong password, an already-registered email, malformed JSON or an unknown status value, because those errors are not yet mapped to their own status codes. |

---

## Data model

**`Application`** (table `applications`)

| Field         | Type                | Notes                                         |
| ------------- | ------------------- | --------------------------------------------- |
| `id`          | `Long`              | Generated                                     |
| `company`     | `String`            | Required                                      |
| `roleTitle`   | `String`            | Required                                      |
| `location`    | `String`            | Optional                                      |
| `status`      | `ApplicationStatus` | Required, stored as its name                  |
| `appliedDate` | `LocalDate`         | Optional                                      |
| `notes`       | `String`            | Optional, column length 2000                  |
| `user`        | `User`              | Owner (`user_id` foreign key), not exposed by the API |

**`ApplicationStatus`:** `APPLIED`, `OA` (online assessment), `INTERVIEW`, `OFFER`, `REJECTED`.

**`User`** (table `users`): `id`, `email` (unique), `passwordHash` (BCrypt).

---

## Running it

**Requirements:** JDK 21 or newer. The Maven wrapper is included, so Maven itself is not
needed.

```bash
./mvnw spring-boot:run
```

The API listens on <http://localhost:8080>.

### Configuration

Everything is in `src/main/resources/application.yml`:

- **Database:** in-memory H2 at `jdbc:h2:mem:internships` (PostgreSQL compatibility mode),
  user `sa`, empty password. Hibernate creates the tables on startup (`ddl-auto: update`).
  Data is lost when the app stops.
- **H2 console:** <http://localhost:8080/h2-console> with JDBC URL `jdbc:h2:mem:internships`,
  user `sa`, no password. It is open without login, so it is for local development only.

Environment variables:

| Variable                 | Default                         | Purpose                                     |
| ------------------------ | ------------------------------- | ------------------------------------------- |
| `JWT_SECRET`             | a random key made at startup    | Key that signs login tokens. At least 32 bytes. Unset, every restart makes a new key and logs everyone out. **Set it anywhere other than your own machine**, for example `export JWT_SECRET="$(openssl rand -base64 48)"`. Anyone who knows the key can mint valid tokens. |
| `JWT_EXPIRATION_MINUTES` | `60`                            | How long a token stays valid                |

To use a persistent database instead of H2, add its JDBC driver to `pom.xml` and set
`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`.

### Tests

```bash
./mvnw test
```

The suite is currently a single test that boots the whole application against the in-memory
database.

---

## Example session

With the app running:

```bash
# 1. Register
curl -i -X POST localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"ada@example.com","password":"correct-horse-42"}'
# HTTP/1.1 201

# 2. Log in and keep the token
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"ada@example.com","password":"correct-horse-42"}' \
  | sed -E 's/.*"token":"([^"]+)".*/\1/')

# 3. Create an application
curl -s -X POST localhost:8080/api/applications \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"company":"Acme","roleTitle":"Software Engineering Intern","location":"Remote",
       "status":"APPLIED","appliedDate":"2026-10-01","notes":"Referred by a friend"}'
# {"id":1,"company":"Acme","roleTitle":"Software Engineering Intern","location":"Remote",
#  "status":"APPLIED","appliedDate":"2026-10-01","note":"Referred by a friend"}

# 4. List your applications (optionally ?status=APPLIED&company=ac&sort=appliedDate,desc)
curl -s localhost:8080/api/applications -H "Authorization: Bearer $TOKEN"
# {"content":[{"id":1,"company":"Acme",...}],"totalElements":1,"totalPages":1,"number":0,"size":20,...}
```

---

## License

MIT — see [LICENSE](LICENSE).
