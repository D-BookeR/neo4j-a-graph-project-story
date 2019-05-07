:begin
CREATE CONSTRAINT ON (o:Order) ASSERT o.orderId IS UNIQUE;
:commit