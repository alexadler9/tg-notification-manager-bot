# Notification manager bot
Telegram bot for the simplest management of notifications about user tasks.

To add a new task, you must enter a message in the format:  
<i>dd.MM.yyyy hh:mm message text</i>  
For example:  
<i>01.01.2022 12:00 walk the dog</i>

The following commands are also available to the telegram bot:  
* <b>/get</b> - get a list of all tasks;
* <b>/delete</b> - delete all tasks;
* <b>/delete ID</b> - delete a task with identifier [ID].

# Building/running app
Specify in application.properties:
- spring.datasource.url
- spring.datasource.username
- spring.datasource.password
- telegram.bot.token

Build and run the application.
