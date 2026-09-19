INSERT INTO
  account (accountid, username)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'alice'),
  ('22222222-2222-2222-2222-222222222222', 'bob'),
  ('33333333-3333-3333-3333-333333333333', 'charlie'),
  ('44444444-4444-4444-4444-444444444444', 'diana');

INSERT INTO
  domain (domain, domainid)
VALUES
  ('github.com', 1),
  ('youtube.com', 2),
  ('news.ycombinator.com', 3),
  ('wikipedia.org', 4);

INSERT INTO
  post (postid, title, path, accountid, domainid)
VALUES
  (
    1,
    'PostgreSQL Internals',
    '/articles/postgresql-internals',
    '11111111-1111-1111-1111-111111111111',
    1
  ),
  (
    2,
    'Understanding TCP',
    '/watch?v=tcp123',
    '22222222-2222-2222-2222-222222222222',
    2
  ),
  (
    3,
    'A History of Unix',
    '/items/123456',
    '11111111-1111-1111-1111-111111111111',
    3
  ),
  (
    4,
    'How Operating Systems Work',
    '/wiki/Operating_system',
    '33333333-3333-3333-3333-333333333333',
    4
  ),
  (
    5,
    'Building a Database',
    '/articles/building-a-database',
    '44444444-4444-4444-4444-444444444444',
    1
  );

INSERT INTO
  comment (commentid, comment, postid, ownerid)
VALUES
  (
    1,
    'This is a really good explanation.',
    1,
    '22222222-2222-2222-2222-222222222222'
  ),
  (
    2,
    'The section about indexes was interesting.',
    1,
    '33333333-3333-3333-3333-333333333333'
  ),
  (
    3,
    'I had never thought about TCP this way.',
    2,
    '11111111-1111-1111-1111-111111111111'
  ),
  (
    4,
    'Unix history is fascinating.',
    3,
    '44444444-4444-4444-4444-444444444444'
  ),
  (
    5,
    'Agreed.',
    3,
    '22222222-2222-2222-2222-222222222222'
  ),
  (
    6,
    'Very useful reference.',
    4,
    '11111111-1111-1111-1111-111111111111'
  ),
  (
    7,
    'This is exactly what I was looking for.',
    5,
    '33333333-3333-3333-3333-333333333333'
  );

INSERT INTO
  upvote (accountid, postid)
VALUES
  ('11111111-1111-1111-1111-111111111111', 2),
  ('22222222-2222-2222-2222-222222222222', 1),
  ('33333333-3333-3333-3333-333333333333', 1),
  ('44444444-4444-4444-4444-444444444444', 1),
  ('11111111-1111-1111-1111-111111111111', 3),
  ('22222222-2222-2222-2222-222222222222', 3),
  ('33333333-3333-3333-3333-333333333333', 4),
  ('44444444-4444-4444-4444-444444444444', 5);