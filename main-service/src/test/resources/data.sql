INSERT INTO categories(name)
VALUES
  ('concerts'),
  ('cinemas');

INSERT INTO events(category_id)
SELECT id
FROM categories
WHERE name = 'concerts';
