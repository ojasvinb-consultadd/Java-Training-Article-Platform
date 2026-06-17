--add new column to articles table, create a generated column which automatically
--fills in data as new columns are added
ALTER TABLE articles
    ADD COLUMN search_vector tsvector
        GENERATED ALWAYS AS (
            to_tsvector('english', coalesce(title,'') || ' ' || coalesce(body,''))
            ) STORED;


--create GIN index
CREATE INDEX idx_articles_search
on articles
using GIN(search_vector);

