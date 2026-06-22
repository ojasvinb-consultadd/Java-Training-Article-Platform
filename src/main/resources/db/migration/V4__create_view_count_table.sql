CREATE TABLE article_view_counts (
        article_id BIGINT PRIMARY KEY,
        view_count BIGINT DEFAULT 0,
        updated_at TIMESTAMP
);