CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

INSERT INTO company VALUES (1, 'Nike'),
                           (2, 'Adidas'),
						   (3, 'BMW'),
						   (4, 'AUDI'),
						   (5, 'Ferrari');
						   
INSERT INTO person VALUES (1, 'Anton', 1),
                           (2, 'Irina', 1),
						   (3, 'Oleg', 5),
						   (4, 'Filip', 5),
						   (5, 'Dina', 5),	
						   (6, 'Elena', 3),
                           (7, 'Sergey', 4),
						   (8, 'Nina', 1),
						   (9, 'Ivan', 2),
						   (10, 'Roman', 2);	
						   
SELECT p.name, c.name AS company
FROM person AS p
LEFT JOIN company AS c ON p.company_id = c.id
WHERE c.id != 5;						

SELECT * FROM
(SELECT c.name, COUNT(*) as people_count
        FROM person p
		JOIN company c ON c.id = p.company_id
        GROUP BY c.name) AS pc
		WHERE people_count = (SELECT max(people_count) FROM 
							       (SELECT c.name, COUNT(*) as people_count
        							FROM person p
									JOIN company c ON c.id = p.company_id
        							GROUP BY c.name) as pc)
		


