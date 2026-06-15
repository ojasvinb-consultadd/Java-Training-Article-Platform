create table articles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    author_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    CONSTRAINT fk_articles_user
                     FOREIGN KEY (author_id)
                     REFERENCES users(id)
                     ON DELETE SET NULL
);

create table tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

create table article_tags (
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,

    PRIMARY KEY (article_id,tag_id),

    CONSTRAINT fk_article_tags_articles
                          FOREIGN KEY (article_id)
                          REFERENCES articles(id)
                          ON DELETE CASCADE,

    CONSTRAINT fk_article_tags_tags
                          FOREIGN KEY (tag_id)
                          REFERENCES tags(id)
                          ON DELETE CASCADE

);

CREATE INDEX idx_articles_author_id
    ON articles(author_id);

CREATE INDEX idx_articles_deleted_at
    ON articles(deleted_at);

CREATE INDEX idx_article_tags_tag_id
    ON article_tags(tag_id);