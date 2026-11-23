# Біржа криптовалют

Android-застосунок: 30 найбільших криптовалют із цінами в USD, локальне збереження та фонове оновлення.

Автор — Зашкольна Дарія 
## Архітектура
Чиста архітектура + MVVM, один Gradle-модуль, пакети:
- `domain` — модель, інтерфейси репозиторію й планувальника, сценарії; без залежностей від Android.
- `data` — Retrofit API, DTO, Room (Entity, DAO), репозиторій, Worker і планувальник WorkManager.
- `presentation` — ViewModel, стан екрана, Compose.

Потік даних:
```
WorkManager → SyncWorker → SyncCryptosUseCase → CryptoRepository → CoinGecko API → Room
Room → Flow → CryptoRepository → ViewModel → StateFlow → Compose
```
Єдине джерело даних для UI — Room. DTO, Entity і доменна модель — окремі типи.

## API
CoinGecko (використано замість CoinDesk), запит:
`GET https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=30&page=1&sparkline=false`
Ключ передається заголовком `x-cg-demo-api-key`.

## Фонова синхронізація
- Періодично: раз на 1 годину, `enqueueUniquePeriodicWork` з іменем `crypto_periodic_sync` і політикою `KEEP`.
- Вручну: кнопка «Оновити», `enqueueUniqueWork` з політикою `KEEP`.
- Обидві задачі потребують `NetworkType.CONNECTED`, повтори з експоненційним backoff (до 5 спроб); помилки ключа не повторюються.
- WorkManager не гарантує точного часу запуску, тому в застосунку показано фактичний час останнього збереження.

## Запуск
1. Відкрийте проєкт в Android Studio.
2. Отримайте Demo API key на coingecko.com (Developer Dashboard).
3. Скопіюйте `local.properties.example` у `local.properties` (файл уже є після відкриття проєкту — додайте до нього рядок):
   `COINGECKO_API_KEY=ваш_ключ`
4. Виконайте Gradle Sync і запустіть застосунок.

Файл `local.properties` виключений з Git.

## Обмеження
Помилки періодичної фонової задачі не відображаються окремо; статус помилки показується за результатом ручного оновлення.
