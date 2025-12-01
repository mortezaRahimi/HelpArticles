# HelpArticles
Android app to show KMP cached articles .

# Architecture 
 - MVVM with respect to Clean




# DI
- Manual dependency injection implemented regards to the light weight builds.


   
# KMP Cache
 -KMP module defined for caching feature using in memory maps.


 
# Auto refresh mechanism:
 - When connectivity returns ,if no cached data -> app will automatically reload article summaries . If cached data exist and stale will reload articles also.



# Staleness rule
- Staleness usecase check if cached data is 1 minute old or not if so then it flagged as stales and new data will reloaded.



# Background fetch
- Background fetch happens once in a day if has network and battery using WorkManager.



# Error handling
- Backend errors handled by predicted ids.
- Connectivity exception also handle with id and android conectivity manager.



# Data
-Data created by mocked Http interceptor.



# Animation is skipped.
  
