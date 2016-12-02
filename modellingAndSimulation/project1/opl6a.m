%oplossing 6
function [Asvd, Aqr, eSvd2, eQr2, eSvdF, eQrF]=opl6a(A,k)
[Q R P]=qr(A);
Aqr=  Q(1:end,1:k)*R(1:k,1:end)*transp(P);
eQr2=norm(A-Aqr,2);
eQrF=norm(A-Aqr,'fro');

[U S V]=svd(A);
Asvd=zeros(size(A,1), size(A,2));
for i=1:k
Asvd=  Asvd+ S(i,i)*U(1:end,i)*transp(V(1:end,i));
end;
eSvd2=norm(A-Asvd,2);
eSvdF=norm(A-Asvd,'fro');

end


