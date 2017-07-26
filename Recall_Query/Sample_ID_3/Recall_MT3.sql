SELECT count(*) 
FROM
(
(
(
SELECT X.type as type1, X.name as name1, X.startline as startline1, X.endline as endline1, Y.type as type2, Y.name as name2, Y.startline as startline2, Y.endline as endline2
FROM
(
SELECT *
FROM clones
WHERE syntactic_type = 3 and similarity_line >= 0.5 and similarity_line < 0.7 and functionality_id = 3
)  as clean_clones, functions as X, functions as Y
WHERE clean_clones.function_id_one = X."id" AND clean_clones.function_id_two = Y."id"
) 
UNION
(
SELECT X.type as type1, X.name as name1, X.startline as startline1, X.endline as endline1, Y.type as type2, Y.name as name2, Y.startline as startline2, Y.endline as endline2
FROM
(
SELECT *
FROM clones
WHERE syntactic_type = 3 and similarity_line >= 0.5 and similarity_line < 0.7 and functionality_id = 3
) as clean_clones, functions as X, functions as Y
WHERE clean_clones.function_id_one = Y."id" AND clean_clones.function_id_two = X."id"
)  
) 
INTERSECT
(
SELECT type1, name1,  startline1, endline1, type2, name2, startline2, endline2
FROM "tools_clones"
)
) as R
