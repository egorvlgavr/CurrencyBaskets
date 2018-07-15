Currency Baskets
--------------- 
[![Build Status](https://travis-ci.org/egorvlgavr/currency-baskets.svg?branch=master)](https://travis-ci.org/egorvlgavr/currency-baskets)

Old good monolithic spring boot application that store and visualize information about several account with different currencies that stored in different banks.
Store all history of account changes in *incremental* way to give ability to view all amount changes or currency rate changes.  
PostgreSQL [database model](https://repository.genmymodel.com/egorvlgavr/CurrencyBaskets) 
### Technologies
1. Core stack: **Spring boot**, **Spring data jpa**, **Spring MVC**, **Thymleaf** 
2. DB: **PostgreSQL**
3. Web: **Bootstrap**, **Chart.js**, **DataTables.bootstrap**
4. CICD: **Docker**, **Travis CI**
### Docker description
Contains:  
1. PostgreSQL 9.4 image based on Alpine Linux
