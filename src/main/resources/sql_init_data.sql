INSERT INTO currency (id, name, abbr)
VALUES
  (1, 'US Dollar','USD'),
  (2, 'Euro', 'EUR'),
  (3, 'Indian Rupees', 'INR'),
  (4, 'Zlotty', 'PLN');

INSERT INTO transaction_status (id, name)
VALUES
       (1, 'New'),
       (2, 'Processing'),
       (3, 'Failed'),
       (4, 'Succeed');

INSERT INTO account (owner_name, balance, currency_id)
VALUES
  ('Manish Kumar', 1000.5, 4),
  ('Ashish Kumar', 1000.5, 4),
  ('Klaudia Oplia', 1000.5, 4);