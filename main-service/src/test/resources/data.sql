INSERT INTO categories(name)
VALUES
  ('concerts'),
  ('cinemas');

INSERT INTO events(category_id)
SELECT id
FROM categories
WHERE name = 'concerts';

INSERT INTO users(name, email)
VALUES
    ('First User', 'first@test.com'),
    ('Second User', 'second@test.com');
