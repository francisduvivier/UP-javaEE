%oplossing 6 b) en c)
function [Asvd, Aqr, Esvd2, Eqr2, EsvdF, EqrF]=opl6draw(A,k)
if(~exist('A')||~exist('k'))
    load A;
    k=size(A,1);
end;
if(k==0)
    k=size(A,1);
end;

nbPixels=size(A,1)*size(A,2);
Eqr2=zeros(k,1);
Esvd2=zeros(k,1);
EqrF=zeros(k,1);
EsvdF=zeros(k,1);
[U S V]=svd(A);
Asvd=A*0;
Aqr=A*0;
[Q R P]=qr(A);

figure(2);
subplot(2,1,1);
hold off;
colormap(gray);
subplot(2,1,2);
hold off;
colormap(gray);
svdFound=0;
qrFound=0;

for i=1:k
        Asvd=  Asvd+ S(i,i)*U(1:end,i)*transp(V(1:end,i));
        diffSVD=A-Asvd;
        eSvd2=norm(diffSVD,2);
        Esvd2(i)=eSvd2;
        eSvdF=norm(diffSVD,'fro');
        EsvdF(i)=eSvdF;
        Aqr=  Q(1:end,1:i)*R(1:i,1:end)*transp(P);
        diffQR=A-Aqr;
        eQr2=norm(diffQR,2);
        Eqr2(i)=eQr2;
        eQrF=norm(diffQR,'fro');
        EqrF(i)=eQrF;

    %we plotten het beeld voor SVD enkel op het moment dat de 2-norm fout per
%pixel een bepaalde waarde heeft
if(eSvd2/nbPixels<=0.0003&&Esvd2(i-1)/nbPixels>0.0003)
    figure(2);
    subplot(2,1,1);
    imagesc(Asvd);
    ylabel('svd');
    set(gca,'xtick',[],'ytick',[]);
    xSvdStr=['k=' int2str(i) ', 2-norm fout='  int2str(eSvd2)  ];
    xlabel(xSvdStr);
    svdFound=1;
end
%we plotten het beeld voor QR ook bij bepaalde relatieve 2-norm fout
if(eQr2/nbPixels<=0.0006&&Eqr2(i-1)/nbPixels>0.0006)
    figure(2);
    subplot(2,1,2);
    imagesc(Aqr);
    ylabel('qr');
    set(gca,'xtick',[],'ytick',[]);
    xlabel(['k=' int2str(i) ', 2-norm fout=' int2str(eQr2)]);
    qrFound=1;
end
if(qrFound==1&&svdFound==1)
break;
end
end
%we plotten de relatieve fouten
figure(1);
hold off;
plot(Esvd2(1:k-1));
hold on;
plot(Eqr2(1:k-1),'r');
legend('SVD met 2-norm','QR met 2-norm')
xlabel('k');
ylabel('fout berekend met 2-norm');

figure(4);
hold off;
plot(EsvdF(1:k-1),'g');
hold on;
plot(EqrF(1:k-1),'y');
legend('SVD met F-norm','QR met F-norm');
xlabel('k');
ylabel('fout berekend met Frobenius norm');
    figure(3);
    imagesc(A);
    colormap(gray);
end