Procedure to follow to create a game
<skip>
**use `?create` to create a new lobby.**

As the creator, you are the only one who's allowed to configure the game

<skip>
**define which question set you want to work with using either `?compiled-question-set` or `?zip-question-set`.**

For both of them you can either attach a link to the question file or attach it directly
<skip>
**add rounds to your game with `?add-round <round-type> <number_of_times>`.**

Example:
```
?add-round classical 2
?add-round poll
?add-round wkejrlkwjerlk
```
This will queue 2 classical rounds, 1 poll round (1 is the number by default) and 1 individual round (which is the round by default when the round-type is invalid)
<skip>
**add question filters with `?add-query [and-query|or-query] [directory|tag|difficulty] <value>` (optional).**

Example:
 ```
?add-query and_query tag Matrice
?add-query or_query tag Eigenvalue
```
The first query adds the filter `tag=Matrice`. Since it is the very first query, whether you use `and_query` or `or_query` doesn't matter. The second query adds the filter `tag=Eigenvalue`. Since it is an `or_query`, it means only questions that **either** have the tag `Matrice` or `Eigenvalue` (or both) will be part of the game
<skip>
**use `?start` to start the game.**

When a round is finished, use `?next` to vote for continuing, or `?force-next` to enforce continuation.