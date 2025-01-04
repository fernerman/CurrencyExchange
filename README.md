# Currency Exchange API

## Overview
This project provides a RESTful API for managing currencies and exchange rates, as well as calculating currency conversions. It uses **SQLite** as the database for simplicity and ease of deployment.

---

## Database Schema

### SQLite Database
The database consists of two tables: `Currencies` and `ExchangeRates`.

### Table: `Currencies`
| Column   | Type     | Description                                    |
|----------|----------|------------------------------------------------|
| `ID`     | `int`    | Currency ID (primary key, auto-increment).      |
| `Code`   | `varchar`| ISO currency code (e.g., USD, EUR).             |
| `FullName`| `varchar`| Full name of the currency.                    |
| `Sign`   | `varchar`| Symbol of the currency.                        |

#### Example:
| ID | Code | FullName            | Sign |
|----|------|---------------------|------|
| 1  | AUD  | Australian Dollar   | A$   |

#### Indexes:
- Primary key: `ID`
- Unique index: `Code` (ensures unique currency codes).

### Table: `ExchangeRates`
| Column            | Type         | Description                                        |
|-------------------|--------------|----------------------------------------------------|
| `ID`              | `int`        | Exchange rate ID (primary key, auto-increment).    |
| `BaseCurrencyId`  | `int`        | Foreign key referencing `Currencies.ID`.           |
| `TargetCurrencyId`| `int`        | Foreign key referencing `Currencies.ID`.           |
| `Rate`            | `decimal(6)` | Exchange rate from base to target currency.        |

#### Indexes:
- Primary key: `ID`
- Unique index: `(BaseCurrencyId, TargetCurrencyId)` (ensures unique currency pairs).

---

## REST API

### 1. Currencies

#### **GET /currencies**
Retrieve a list of all currencies.

**Response Example:**
```
json
[
    { "id": 1, "name": "United States Dollar", "code": "USD", "sign": "$" },
    { "id": 2, "name": "Euro", "code": "EUR", "sign": "€" }
]
```
GET /currency/{code}
Retrieve details of a specific currency by its code.

Response Example:
{ "id": 2, "name": "Euro", "code": "EUR", "sign": "€" }
POST /currencies
Add a new currency. Send data as x-www-form-urlencoded.

Request Fields:

name: Full name of the currency.
code: ISO currency code.
sign: Symbol of the currency.
Response Example:

json
Копировать код
{ "id": 3, "name": "Pound Sterling", "code": "GBP", "sign": "£" }
2. Exchange Rates
GET /exchangeRates
Retrieve a list of all exchange rates.

Response Example:
[
    {
        "id": 1,
        "baseCurrency": { "id": 1, "name": "USD", "code": "USD", "sign": "$" },
        "targetCurrency": { "id": 2, "name": "EUR", "code": "EUR", "sign": "€" },
        "rate": 0.99
    }
]
GET /exchangeRate/{baseCode}{targetCode}
Retrieve the exchange rate between two currencies.

Response Example:
{
    "id": 1,
    "baseCurrency": { "id": 1, "name": "USD", "code": "USD", "sign": "$" },
    "targetCurrency": { "id": 2, "name": "EUR", "code": "EUR", "sign": "€" },
    "rate": 0.99
}
POST /exchangeRates
Add a new exchange rate. Send data as x-www-form-urlencoded.

Request Fields:

baseCurrencyCode: ISO code of the base currency.
targetCurrencyCode: ISO code of the target currency.
rate: Exchange rate from base to target currency.
Response Example:
{
    "id": 2,
    "baseCurrency": { "id": 1, "name": "USD", "code": "USD", "sign": "$" },
    "targetCurrency": { "id": 3, "name": "GBP", "code": "GBP", "sign": "£" },
    "rate": 0.72
}
PATCH /exchangeRate/{baseCode}{targetCode}
Update an existing exchange rate. Send data as x-www-form-urlencoded.

Request Field:

rate: New exchange rate.
Response Example:
{
    "id": 2,
    "baseCurrency": { "id": 1, "name": "USD", "code": "USD", "sign": "$" },
    "targetCurrency": { "id": 3, "name": "GBP", "code": "GBP", "sign": "£" },
    "rate": 0.75
}
3. Currency Exchange
GET /exchange
Calculate the conversion of an amount from one currency to another.

Query Parameters:

from: Base currency code.
to: Target currency code.
amount: Amount to convert.
Response Example:
{
    "baseCurrency": { "id": 1, "name": "USD", "code": "USD", "sign": "$" },
    "targetCurrency": { "id": 3, "name": "GBP", "code": "GBP", "sign": "£" },
    "rate": 0.75,
    "amount": 100,
    "convertedAmount": 75
}
Error Handling
{ "message": "Error description" }
Currency not found" (404)
"Exchange rate not found" (404)
"Invalid request parameters" (400)
"Database unavailable" (500)
