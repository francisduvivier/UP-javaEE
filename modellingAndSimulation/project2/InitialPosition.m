function out = InitialPosition(in)

s = RandStream('mt19937ar','Seed',in);
theta = 2*pi*rand(s,1);

out=15*[cos(theta),sin(theta)];