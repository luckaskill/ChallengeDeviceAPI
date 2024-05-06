# challenge-api

## Description

Rest service for managing devices. The API allows to create, update, delete and search devices by filter.

## Running the project

Navigate to the project root and run the following command from CLI:

```sh
docker-compose up -d
```

## API

**Date time format example: 2022-01-01T00:00:00**

### GET /device

Retrieves all devices with or without filter parameters.

**Query Parameters:**

- **brand**: Filters devices by brand (string)
- **from**: Filters devices created at or after this date and time.
- **to**: Filters devices created at or before this date and time.

**Success Response:**

- **Code:** 200 OK
- **Content:**

```
[
  {
    "id": "1",
    "brand": "Brand Name",
    "name": "Device Name",
    "createdAt": "2022-01-01T00:00:00",
    "lastUpdate": "2022-01-01T00:00:00"
  },
  {
    "id": "1",
    "brand": "Brand Name2",
    "name": "Device Name2",
    "createdAt": "2022-01-01T00:00:00",
    "lastUpdate": "2022-01-01T00:00:00"
  },
]
```

### GET /device/{id}

Retrieves a specific device by its ID.

**Success Response:**

- **Code:** 200 OK
- **Content:**

```
{
    "id":"1", 
    "brand":"Brand Name", 
    "name":"Device Name", 
    "createdAt":"2022-01-01T00:00:00", 
    "lastUpdate":"2022-01-01T00:00:00"
}
```

**Not Found Response**

- **Code:** 404 Not Found

### POST /device

Creates a new device.

**Request Body:**

```
{
    "brand": "Brand Name",
    "name": "Device Name"
}
```

**Success Response:**

- **Code:** 200 OK
- **Content:** `ffffffffffffffffffffffff`

### PUT /device

Updates an existing device.

**Request Body:**

```
{
    "id": "ffffffffffffffffffffffff",
    "brand": "Brand Name",
    "name": "Device Name"
}
```

**Success Response:**

- **Code:** 200 OK

### DELETE /device/{id}

Deletes a device by ID.

**Success Response:**

- **Code:** 200 OK

challenge-completed