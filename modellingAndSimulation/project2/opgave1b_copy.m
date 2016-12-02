function [ meanList ] = opgave1b( input_args )
%OPGAVE1B Summary of this function goes here
%   Detailed explanation goes here
meanDiff=0;
nbSteps=4;
nbChecks=5;
nbSimsi=zeros(nbSteps,1);
posList;
for nbStepsi=1:nbSteps
    nbSimsi(nbStepsi)=1;
    while(nbSimsi(nbStepsi)<=nbChecks||meanDiff>1)
        [newPosList newDistList]=opgave1a(nbStepsi,2^nbSimsi(nbStepsi),1);
        
        newMean = mean(newDistList,1);
        meanList(nbStepsi,nbSimsi(nbStepsi))=newMean;
        meanDiff=0;
        %We check the 4 last mean and see how much difference there is
       
        if(nbSimsi(nbStepsi)>=nbChecks)
        for j=0:nbChecks-2
            meanDiff= meanDiff+ (meanList(nbStepsi,nbSimsi(nbStepsi)-j)-meanList(nbStepsi,nbSimsi(nbStepsi)-j-1))^2;
        end
        end
        
        nbSimsi(nbStepsi)=nbSimsi(nbStepsi)+1;
    end
    posList(nbSteps)=newPostlist;
   nbSimsi(nbStepsi)=nbSimsi(nbStepsi)-1;

end

hold off;
for nbStepsi=1:nbSteps
    subplot(2,2,nbStepsi);
    plot(meanList(nbStepsi,1:nbSimsi(nbStepsi)));
    xlabel('i=log2(N)');
    ylabel(strcat('lastVal: ',num2str(meanList(nbStepsi,nbSimsi(nbStepsi)))) );

    legend(strcat('For:  ', num2str(nbStepsi),' steps,  2\^', num2str(nbSimsi(nbStepsi)), ' simulations' ));
end

drawCircle(20,2)




end