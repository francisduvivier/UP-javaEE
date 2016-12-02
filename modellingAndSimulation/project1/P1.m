% oplossing 2 tot en met 5
classdef P1
    methods (Static)
        function [A]=makeA
            A=diag(20-(0:19))+diag(-ones(19,1),-1)+diag(-ones(19,1),1);
        end
        function [n newA]=opl2(mode)
            A=P1.makeA;
            [n newA]=P1.opl2Gnrc(A,mode);
        end
        function [n newA]=opl2Gnrc(A,mode)
            newA=A;
            n=0;
            while(not(P1.isTriangle(newA)))
                newA=P1.getNextA(newA,mode);
                n=n+1;
            end
        end
        function [nextA]= getNextA(A,mode)
            if(mode==1)
                k=0;
            elseif(mode==2)
                k=A(end);
                
            else %mode==3
                am=A(end);
                am1=A(end-1,end-1);
                b=A(end,end-1);
                d=(1/2)*(am1-am);
                k=am-((sign(d)*b^2)/(abs(d)+sqrt(d^2+b^2)));
                
            end
            
            kI=k*eye(size(A,1));
            [Q R]=qr(A-kI);
            nextA=R*Q+kI;
            
        end
        function [isTrue]=isTriangle(A)
            isTrue=norm(A-triu(A),1)<=eps;
        end
        
        function [allAms0 allAms1]=opl3
            hold off;
            allAms0=P1.opl3Gnrc(0);
            allAms1=P1.opl3Gnrc(1);
        end
        
        %Offset is de
        function [allAms]=opl3Gnrc(offset)
            
            for mode=1:3
                newA=P1.makeA;
                goal=eig(newA);
                goal=goal(offset+1);
                n=0;
                while (n<2||abs(allAms(n,mode)-goal)>=0.1*10^(-10))
                    n=n+1;
                    allAms(n,mode)=newA(end-offset,end-offset);
                    newA=P1.getNextA(newA,mode);
                end
                
                %end set maxN for plotting
                toPlot=abs(goal-allAms(1:n,mode));
                P1.plot(2,1,offset+1,1,toPlot,mode);
                xlabel(['De convergentie van a' int2str(20-offset) ',' int2str(20-offset) 'voor bepaalde k']);
                ylabel(['eigenwaarde-a' int2str(20-offset) ',' int2str(20-offset)])
            end
            legend('QR','QR met shift', 'QR met alternatieve shift');
        end
        
        function opl4
            for i=(1:5)
                if(i/2~=round(i/2))
                P1.opl4Gnrc(3,1,(i+1)/2,20-i+1);
                end
                
            end
        end
        
        function [allBs]=opl4Gnrc(yPlots,xPlots,plotNb,elNo)
            
            for mode=1:3
                newA=P1.makeA;
                n=0;
                goal=0;
                while (n<2||abs(allBs(n,mode)-goal)>=0.1*10^(-10))
                    n=n+1;
                    allBs(n,mode)=newA(elNo,elNo-1);
                    newA=P1.getNextA(newA,mode);
                end
                toPlot=abs(goal-allBs(1:n,mode));
                
                P1.plot(yPlots,xPlots,plotNb,1,toPlot,mode);
                xlabel(['De convergentie van a' int2str(elNo) ',' int2str(elNo-1) 'voor bepaalde k']);
            end               

            legend('QR','QR met shift', 'QR met alternatieve shift');
        end
        
        function plot(yPlots,xPlots,plotNb,figureNb,toPlot,mode)
            
            %set graph color
            if(mode==1)
                color='r';
            elseif(mode==2)
                color='b';
            else %mode==3
                color='g';
            end
            %end set graph color
            figure(figureNb);
            subplot(yPlots, xPlots,plotNb);
            semilogy(toPlot,color);
            hold on;
        end
        
        function opl5  
        checkNs=[20 19 15];
        i=1;
        for checkN=checkNs
           P1.opl5Gnrc(checkN,3,1,i);
            i=i+1;
             xlabel(['De convergentie van a' int2str(checkN) ',' int2str(checkN)] );
        end
        end
        
        function [allAms]=opl5Gnrc(checkN,yPlots,xPlots,plotNb)
            
            for mode=1:3
                newA=P1.makeA;
                newL=size(newA,1);
                goal=eig(newA);
                goal=goal(size(newA,1)-checkN+1);
                n=0;
                while (newL>1&&(n<2||abs(allAms(n,mode)-goal)>=0.1*10^(-10)))
                   n=n+1;
                   allAms(n,mode)=newA(checkN,checkN);

                    [newL newA]=P1.getNextA2(newL,newA,mode);
                end
               
                %end set maxN for plotting
                toPlot=abs(goal-allAms(1:n,mode));
                P1.plot(yPlots,xPlots,plotNb,1,toPlot,mode);
                
            end
            legend('QR','QR met shift', 'QR met alternatieve shift');
        end
        
        function [nextL nextA]= getNextA2(currL,A,mode)
            if(mode==1)
                k=0;
            elseif(mode==2)
                k=A(currL,currL);
            else %mode==3
                am=A(currL,currL);
                am1=A(currL-1,currL-1);
                b=A(currL,currL-1);
                d=(1/2)*(am1-am);
                k=am-((sign(d)*b^2)/(abs(d)+sqrt(d^2+b^2)));
                
            end
            
            kI=k*eye(size(A,1));
            [Q R]=qr(A-kI);
            nextA=R*Q+kI;
            if(mode~=1)
                if(currL>2&&abs(nextA(currL,currL-1))<eps*10^(-6))
                    
                    nextL=currL-1;
                    nextA(currL,currL-1)=0;
                else
                    
                    nextL=currL;
                end
            else
                nextL=currL;
            end
        end
    end
end