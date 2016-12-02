function [distList posList] = opgave1a(n,N,escapeMode)
%OPGAVE1B Summary of this function goes here
%   Detailed explanation goes here
if(~exist('escapeMode'))
    escapeMode=0; %alle gevallen
end
stepDist=6;
distList=zeros(N,1);
posList=zeros(N,2);
i1=1;

while i1<=N
    init=InitialPosition(215207);
    randX=init(1);
    randY=init(2);
    for i2=1:n
        if (escapeMode==2 && i2==n&& sqrt(randX^2+randY^2)>14&&sqrt(randX^2+randY^2)<26)
            newnewDist=0;
            newrandX =0;
            newrandY =0;
            while(newnewDist<20)
            newrandHoek=rand* 2 * pi;
            newrandX = randX+ cos(newrandHoek) * stepDist;
            newrandY = randY+ sin(newrandHoek) * stepDist;
            newnewDist=sqrt(newrandX^2+newrandY^2);
            end
            randX = newrandX;
            randY = newrandY;
        else
        randHoek=rand* 2 * pi;
        randX = randX+ cos(randHoek) * stepDist;
        randY = randY+ sin(randHoek) * stepDist;
        end
    end
    posList(i1,1)=randX;
    posList(i1,2)=randY;
    newDist=sqrt(randX^2+randY^2);
    if((escapeMode==2||escapeMode==1)&&newDist<20)
        i1=i1-1;
    else
        distList(i1)= newDist;
    end
    i1=i1+1;
end
end
