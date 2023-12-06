# Notification manager bot
Telegram bot for the management of notifications about user tasks.

The bot supports the following operations:
- adding a task in the format <i>dd.MM.yyyy hh:mm message text</i>
- deleting tasks (all or by identifier)
- getting a list of tasks

Every minute the bot checks current tasks and sends out appropriate notifications if necessary.

## Building/running app

Add <i>application-db.properties</i> to the project and specify in it:
- spring.datasource.url
- spring.datasource.username
- spring.datasource.password

Add <i>application-token.properties</i> to the project and specify in it:
- telegram.bot.token

Build and run the application.
