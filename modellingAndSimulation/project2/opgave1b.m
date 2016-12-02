function [ meanList ] = opgave1b( escapeMode )
%OPGAVE1B Summary of this function goes here
%   Detailed explanation goes here
if(~exist('escapeMode'))
    escapeMode=1; %alle gevallen
end

nbSims=10000;
nbSteps=4;
nbChecks=5;
nbSimsi1=zeros(nbSteps,1);
posList=zeros(nbSteps,nbSims, 2);
distList=zeros(nbSteps,nbSims);
meanList=zeros(nbSteps,nbSims);
for nbStepsi=1:nbSteps
    [ distList(nbStepsi,1:end) posList(nbStepsi,1:end,1:end)]=opgave1a(nbStepsi,nbSims,escapeMode);
    EM=mean(distList(nbStepsi,1:end)); %the expected mean
    for i= nbSims -(0:nbSims-1)
        meanList(nbStepsi,i)=mean(distList(nbStepsi,1:i));
        if( nbSimsi1(nbStepsi)==0&&  abs(EM-meanList(nbStepsi,i))>0.01)
            nbSimsi1(nbStepsi)=i+1;
        end
        
        
    end
    
end
figure(1);
for nbStepsi=1:nbSteps
    subplot(2,2,nbStepsi);
    hold off;
    loglog(meanList(nbStepsi,1:end));
    xlabel('i=log2(N)');
    ylabel(strcat('lastVal: ',num2str(meanList(nbStepsi,end))));

    legend(strcat('For:  ', num2str(nbStepsi),' steps,  ', num2str(nbSimsi1(nbStepsi)), ' simulations ar needed' ));
end

%now we drae the positions
figure(2);
for nbStepsi=1:nbSteps
    subplot(2,2,nbStepsi);
    hold off;
    drawCircle(20);
    hold on;
    scatter(posList(nbStepsi,1:end,1),posList(nbStepsi,1:end,2),0.9, 'r' );
        xlabel(strcat('n=', num2str(nbStepsi)));
    axis manual;
    axis([-20 40 -40 20]);
    
end

figure(3);
for nbStepsi=1:nbSteps
    subplot(2,2,nbStepsi);
    hold off;
    hist(distList(nbStepsi,1:nbSimsi1(nbStepsi)));
    xlabel(strcat('n=', num2str(nbStepsi)));

end


end

function  drawCircle( r )
ang=0:0.01:2*pi; 
xp=r*cos(ang);
yp=r*sin(ang);
plot(xp,yp);
end


